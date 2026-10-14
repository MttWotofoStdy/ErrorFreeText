package com.example.errorfreetext.service;

import com.example.errorfreetext.dto.YandexSpellerResponse;
import com.example.errorfreetext.entity.TextTask;
import com.example.errorfreetext.entity.model.TextTaskStatus;
import com.example.errorfreetext.repository.TextTaskRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class YandexSpellerService {
    private final TextTaskRepository textTaskRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final String SPELLER_URL = "https://speller.yandex.net/services/spellservice.json/checkTexts";
    private static final int MAX_CHUNK_SIZE = 10000;
    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");

    public void processTask(TextTask task) {
        try {
            log.info("Начало обработки : {}", task.getId());
            String correctedText = correctText(task.getText(), task.getLanguage().name().toLowerCase());
            task.setResultText(correctedText);
            task.setStatus(TextTaskStatus.COMPLETED);
            task.setErrorMessage(null);
            textTaskRepository.save(task);
            log.info("Задача: {} успешно обработана", task.getId());
        } catch (Exception e) {
            log.error("Ошибка при обработке задачи : {}: {}", task.getId(), e.getMessage(), e);
            task.setStatus(TextTaskStatus.ERROR);
            task.setErrorMessage(e.getClass().getSimpleName() + ": " + e.getMessage());
            textTaskRepository.save(task);
        }
    }

    private String correctText(String text, String lang) throws Exception {
        String options = buildOptions(text);
        List<String> chunks = splitIntoChunks(text, MAX_CHUNK_SIZE);
        StringBuilder result = new StringBuilder();
        for (String chunk : chunks) {
            List<YandexSpellerResponse> errors = callYandexApi(chunk, lang, options);
            result.append(applyCorrections(chunk, errors));
        }
        return result.toString();
    }


    private List<YandexSpellerResponse> callYandexApi(String text, String lang, String options) throws Exception {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("text", text);
        formData.add("lang", lang);
        formData.add("options", options);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                SPELLER_URL,
                HttpMethod.POST,
                request,
                String.class
        );

        log.debug("Raw Yandex Response: {}", response.getBody());

        List<List<YandexSpellerResponse>> listOfLists = objectMapper.readValue(
                response.getBody(),
                new TypeReference<List<List<YandexSpellerResponse>>>() {
                }
        );

        return listOfLists.stream()
                .flatMap(List::stream)
                .toList();
    }

    private String buildOptions(String text) {
        int mask = 0;
        if (DIGIT_PATTERN.matcher(text).find()) mask |= 1;
        if (URL_PATTERN.matcher(text).find()) mask |= 2;
        return String.valueOf(mask);
    }

    private List<String> splitIntoChunks(String text, int maxSize) {
        List<String> textSize = new ArrayList<>();
        if (text.length() <= maxSize) {
            textSize.add(text);
            return textSize;
        }
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxSize, text.length());
            if (end < text.length()) {
                int lastSpace = text.lastIndexOf(' ', end);
                if (lastSpace > start) end = lastSpace + 1;
            }
            textSize.add(text.substring(start, end));
            start = end;
        }
        return textSize;
    }

    private String applyCorrections(String original, List<YandexSpellerResponse> errors) {
        if (errors == null || errors.isEmpty()) {
            return original;
        }
        List<YandexSpellerResponse> validErrors = errors.stream()
                .filter(e -> e.getCode() != 0 && e.getSuggestions() != null && e.getSuggestions().length > 0)
                .sorted(Comparator.comparingInt(YandexSpellerResponse::getPosition))
                .toList();

        if (validErrors.isEmpty()) {
            return original;
        }

        log.debug("Найдено {} ошибок в тексте: {}", validErrors.size(), original);
        for (YandexSpellerResponse err : validErrors) {
            log.debug("  Ошибка: '{}' (pos={}, len={}) → '{}'",
                    err.getWord(), err.getPosition(), err.getLen(),
                    err.getSuggestions() != null && err.getSuggestions().length > 0 ? err.getSuggestions()[0] : "нет вариантов");
        }
        StringBuilder result = new StringBuilder();
        int lastPos = 0;
        for (YandexSpellerResponse err : validErrors) {
            int pos = err.getPosition();
            int len = err.getLen();
            String replacement = err.getSuggestions()[0];
            if (pos < 0 || pos + len > original.length() || pos < lastPos) {
                log.warn("Пропускаем некорректную ошибку: pos={}, len={}, lastPos={}", pos, len, lastPos);
                continue;
            }

            result.append(original, lastPos, pos);

            result.append(replacement);
            lastPos = pos + len;
        }
        if (lastPos < original.length()) {
            result.append(original.substring(lastPos));
        }

        String correctedText = result.toString();
        log.debug("Исправленный текст: {}", correctedText);

        return correctedText;
    }
}
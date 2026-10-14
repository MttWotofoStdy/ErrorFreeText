package com.example.errorfreetext.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class YandexSpellerServiceLogicTest {

    private final YandexSpellerService service = new YandexSpellerService(
            null, new ObjectMapper(), null
    );

    @Test
    @DisplayName("не разбиваться на части")
    void splitIntoChunks_shortText_shouldReturnOneChunk() {
        List<String> chunks = (List<String>) ReflectionTestUtils.invokeMethod(
                service, "splitIntoChunks", "Короткий текст", 10000);

        assertEquals(1, chunks.size());
        assertEquals("Короткий текст", chunks.get(0));
    }

    @Test
    @DisplayName("разбиваться по пробелам")
    void splitIntoChunks_longText_shouldSplitBySpace() {
        String longText = "Слово1 ".repeat(2000); // ~14000 символов
        List<String> textSize = (List<String>) ReflectionTestUtils.invokeMethod(
                service, "splitIntoChunks", longText.trim(), 10000);

        assertTrue(textSize.size() > 1);
             textSize.forEach(chunk -> assertTrue(chunk.length() <= 10000));
    }

    @Test
    @DisplayName("при наличии цифр")
    void buildOptions_withDigits_shouldIncludeIgnoreDigits() {
        String options = (String) ReflectionTestUtils.invokeMethod(
                service, "buildOptions", "Тест 123");

        assertTrue(options.contains("1"));
    }

    @Test
    @DisplayName("при наличии ссылки")
    void buildOptions_withUrl_shouldIncludeIgnoreUrls() {
        String options = (String) ReflectionTestUtils.invokeMethod(
                service, "buildOptions", "Перейди на https://example.com");

        assertTrue(options.contains("2"));
    }

    @Test
    @DisplayName("будет '0'")
    void buildOptions_plainText_shouldReturnZero() {
        String options = (String) ReflectionTestUtils.invokeMethod(
                service, "buildOptions", "Просто обычный текст");

        assertEquals("0", options);
    }
}
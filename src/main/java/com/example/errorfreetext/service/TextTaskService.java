package com.example.errorfreetext.service;


import com.example.errorfreetext.dto.CreateTextTaskRequest;

import com.example.errorfreetext.entity.TextTask;
import com.example.errorfreetext.entity.model.Language;
import com.example.errorfreetext.repository.TextTaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TextTaskService {

    private final TextTaskRepository textTaskRepository;

    private static final Pattern ONLY_DIGITS_SPECIAL = Pattern.compile("^[^a-zA-Zа-яА-Я]+$");

    protected void validateRequest(CreateTextTaskRequest request) {
        if (request.getLanguage() != Language.RU && request.getLanguage() != Language.EN) {
            throw new IllegalArgumentException("Только RU/EN");
        }

        String text = request.getText();
        if(text==null || text.length() <3 ){
            throw new IllegalArgumentException("Текст меньше 3 символов");
        }

        if(ONLY_DIGITS_SPECIAL.matcher(text).matches()){
            throw new IllegalArgumentException("Текст не содержит символов");
        }
    }

    public TextTask createTextTask(CreateTextTaskRequest request) {
        validateRequest(request);
        return textTaskRepository.save(new TextTask(request));
    }

    public TextTask getTaskById(Long id) {
        return textTaskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("задача не найдена " +id));
    }


}

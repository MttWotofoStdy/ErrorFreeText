package com.example.errorfreetext.service;

import com.example.errorfreetext.dto.CreateTextTaskRequest;
import com.example.errorfreetext.entity.model.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TextTaskServiceTest {

    private TextTaskService textTaskService;
    private CreateTextTaskRequest request;

    @BeforeEach
    void setUp() {
        textTaskService = new TextTaskService(null);

        request = new CreateTextTaskRequest();
        request.setText("Корректный текст для проверки");
        request.setLanguage(Language.RU);
    }

    @Test
    @DisplayName("Без исключений")
    void validateRequest_validRequest_shouldPass() {
        assertDoesNotThrow(() ->
                ReflectionTestUtils.invokeMethod(textTaskService, "validateRequest", request));
    }

    @Test
    @DisplayName("Текст короче 3 символов ")
    void validateRequest_shortText_shouldThrow() {
        request.setText("аб");
        assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(textTaskService, "validateRequest", request));
    }

    @Test
    @DisplayName("Текст только из цифр")
    void validateRequest_digitsOnly_shouldThrow() {
        request.setText("12345");
        assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(textTaskService, "validateRequest", request));
    }

    @Test
    @DisplayName("Текст только из спецсимволов")
    void validateRequest_specialCharsOnly_shouldThrow() {
        request.setText("@#$%^&*()!");
        assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(textTaskService, "validateRequest", request));
    }

    @Test
    @DisplayName("Null язык должен вызывать IllegalArgumentException")
    void validateRequest_nullLanguage_shouldThrow() {
        request.setLanguage(null);
        assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(textTaskService, "validateRequest", request));
    }
}
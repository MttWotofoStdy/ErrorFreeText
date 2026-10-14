package com.example.errorfreetext.dto;

import com.example.errorfreetext.entity.model.Language;
import com.example.errorfreetext.entity.model.TextTaskStatus;

import java.time.LocalDateTime;

public record GetTextTaskResponse(
        Long id,
        Language language,
        TextTaskStatus status,
        String result,
        String error,
        LocalDateTime createdAt
) {}
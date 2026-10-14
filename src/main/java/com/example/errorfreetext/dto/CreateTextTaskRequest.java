package com.example.errorfreetext.dto;

import com.example.errorfreetext.entity.model.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTextTaskRequest {

    @NotBlank
    @Size(min = 3, message = "Текст меньше допустимого (3 символа)")
    private String text;

    @NotNull(message = "Укажите язык текста RU/EN")
    private Language language;
}


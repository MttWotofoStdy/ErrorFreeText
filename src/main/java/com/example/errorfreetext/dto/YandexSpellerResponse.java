package com.example.errorfreetext.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class YandexSpellerResponse {
    private int code;

    @JsonProperty("pos")
    private int position;

    private int len;
    private String word;

    @JsonProperty("s")
    private String[] suggestions;
}
package com.example.errorfreetext.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate rt = new RestTemplate();

        rt.getMessageConverters().forEach(converter -> {
            if (converter instanceof FormHttpMessageConverter formConverter) {
                formConverter.setCharset(StandardCharsets.UTF_8);
            }
        });

        return rt;
    }
}
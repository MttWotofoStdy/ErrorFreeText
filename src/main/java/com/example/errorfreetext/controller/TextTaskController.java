package com.example.errorfreetext.controller;


import com.example.errorfreetext.dto.CreateTextTaskRequest;

import com.example.errorfreetext.dto.GetTextTaskResponse;
import com.example.errorfreetext.entity.TextTask;
import com.example.errorfreetext.service.TextTaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/texttasks")
@RequiredArgsConstructor
@Validated
public class TextTaskController {

    private final TextTaskService textTaskService;

    @PostMapping
    public ResponseEntity<Long> createTask(@Valid @RequestBody CreateTextTaskRequest request) {
        TextTask task = textTaskService.createTextTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(task.getId());
    }

    @GetMapping
    public ResponseEntity<Void> getTasks(){
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    } //лок от пустого запроса


    @GetMapping("/{id}")
    public ResponseEntity<GetTextTaskResponse> getTask(@PathVariable @Positive Long id) { //GlobalHandler не дал мне реализовать перехват неверного аргумента
        if (id < 1) {
            throw new IllegalArgumentException("ID задачи должен быть положительным числом");
        }

        TextTask task = textTaskService.getTaskById(id);


        GetTextTaskResponse response = new GetTextTaskResponse(
                task.getId(),
                task.getLanguage(),
                task.getStatus(),
                task.getResultText(),
                task.getErrorMessage(),
                task.getCreatedAt()
        );
        return ResponseEntity.ok(response);
    }}

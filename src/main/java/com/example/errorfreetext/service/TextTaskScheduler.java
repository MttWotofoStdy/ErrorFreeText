package com.example.errorfreetext.service;

import com.example.errorfreetext.entity.TextTask;
import com.example.errorfreetext.entity.model.TextTaskStatus;
import com.example.errorfreetext.repository.TextTaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TextTaskScheduler {

    private final TextTaskRepository textTaskRepository;
    private final YandexSpellerService yandexSpellerService;

    @Scheduled(fixedDelayString = "${scheduler.check-interval:10000}")
    public void processNewTasks() {
        List<TextTask> newTasks = textTaskRepository.findByStatus(TextTaskStatus.NEW);

        if (newTasks.isEmpty()) {
            log.trace("Новых задач нет");
            return;
        }
        log.info("Найдено {} новых задач", newTasks.size());

        for (TextTask task : newTasks) {
            try {
                yandexSpellerService.processTask(task);

                log.debug("Задача {} успешно переведена в COMPLETED", task.getId());

            } catch (Exception e) {
                log.error("Ошибка при обработке задачи {}: {}",
                        task.getId(), e.getMessage(), e);
                task.setStatus(TextTaskStatus.ERROR);
                task.setErrorMessage("Ошибка обработки: " + e.getMessage());
                textTaskRepository.save(task);
            }
        }
    }
}
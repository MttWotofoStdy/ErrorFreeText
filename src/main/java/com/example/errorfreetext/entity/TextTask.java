package com.example.errorfreetext.entity;

import com.example.errorfreetext.dto.CreateTextTaskRequest;
import com.example.errorfreetext.entity.model.Language;
import com.example.errorfreetext.entity.model.TextTaskStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "text_tasks")
@Getter
@Setter
@NoArgsConstructor
public class TextTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //взял long вместо UUID для быстрой отладки и разработки

    @Column(nullable = false, length = 100000)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TextTaskStatus status;

    @Column(columnDefinition = "TEXT")
    private String resultText;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public TextTask(CreateTextTaskRequest request) {
        this.text = request.getText();
        this.resultText = null;
        this.language = request.getLanguage();
        this.status = TextTaskStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }
}
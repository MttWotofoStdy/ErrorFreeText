package com.example.errorfreetext.repository;

import com.example.errorfreetext.entity.TextTask;
import com.example.errorfreetext.entity.model.TextTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextTaskRepository extends JpaRepository<TextTask, Long> {

    List<TextTask> findByStatus(TextTaskStatus status);


}

package com.example.mvc_with_jpa_1.repository;

import com.example.mvc_with_jpa_1.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}

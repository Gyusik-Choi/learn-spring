package com.example.mvc_with_jpa_1.repository;

import com.example.mvc_with_jpa_1.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c join fetch c.post")
    List<Comment> findAllCommentWithPostFetchJoin();
}

package com.example.mvc_with_jpa_1.repository;

import com.example.mvc_with_jpa_1.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c join fetch c.post")
    List<Comment> findAllCommentWithPostFetchJoin();

    @Query("select c from Comment c join fetch c.post where c.id = :commentId")
    Optional<Comment> findByCommentId(@Param("commentId") Long commentId);

    @Query("select c from Comment c join fetch c.post p where p.id = :postId")
    List<Comment> findCommentWithPostFetchJoinPaging(@Param("postId") Long postId, Pageable pageable);
}

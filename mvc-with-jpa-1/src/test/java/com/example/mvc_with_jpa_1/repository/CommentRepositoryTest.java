package com.example.mvc_with_jpa_1.repository;

import com.example.mvc_with_jpa_1.domain.Comment;
import com.example.mvc_with_jpa_1.domain.Post;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository repository;

    @BeforeEach
    void setUp() {
        Post mockPost = Post.builder().id(1L).title("title").content("content").build();
        postRepository.save(mockPost);

        Comment mockComment1 = Comment.toEntity("content", mockPost);
        Comment mockComment2 = Comment.toEntity("content2", mockPost);
        Comment mockComment3 = Comment.toEntity("content3", mockPost);
        Comment mockComment4 = Comment.toEntity("content4", mockPost);
        repository.save(mockComment1);
        repository.save(mockComment2);
        repository.save(mockComment3);
        repository.save(mockComment4);
    }

    @Test
    @DisplayName("모든 Comment 를 조회한다")
    void findAllCommentWithPostFetchJoin() {
        List<Comment> comments = repository.findAllCommentWithPostJoinFetch();
        Assertions.assertThat(comments.size()).isEqualTo(4);
    }
}
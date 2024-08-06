package com.example.mvc_with_jpa_1.repository;

import com.example.mvc_with_jpa_1.config.CommentEmRepositoryTestConfig;
import com.example.mvc_with_jpa_1.domain.Comment;
import com.example.mvc_with_jpa_1.domain.Post;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@DataJpaTest
// https://jun27.tistory.com/69
// https://velog.io/@choicore/Spring-DDD%EC%97%90%EC%84%9C-Repository-Test-%ED%95%98%EA%B8%B0
// @Repository 어노테이션이 붙은 CommentEmRepository 을 component scan 을 하지 못하는 (bean 으로 등록하지 못하는) 에러 해결
@ContextConfiguration(classes = CommentEmRepositoryTestConfig.class)
public class CommentEmRepositoryTest {

    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired CommentEmRepository commentEmRepository;

    @BeforeEach
    void setUp() {
        Post mockPost = Post.builder().id(1L).title("title").content("content").build();
        postRepository.save(mockPost);

        Comment mockComment1 = Comment.toEntity("content", mockPost);
        Comment mockComment2 = Comment.toEntity("content2", mockPost);
        Comment mockComment3 = Comment.toEntity("content3", mockPost);
        Comment mockComment4 = Comment.toEntity("content4", mockPost);
        commentRepository.save(mockComment1);
        commentRepository.save(mockComment2);
        commentRepository.save(mockComment3);
        commentRepository.save(mockComment4);
    }

    @Test
    @DisplayName("fetch join 을 통한 paging")
    void findAllJoinFetchLimitByEm() {
        Long postId = 1L;
        PageRequest request = PageRequest.of(0, 3);
        List<Comment> commentList = commentEmRepository.findAllJoinFetchLimitByEm(postId, request);

        Assertions.assertThat(commentList.size()).isEqualTo(3);
    }
}


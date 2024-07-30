package com.example.mvc_with_jpa_1.repository;

import com.example.mvc_with_jpa_1.config.CommentEmRepositoryTestConfig;
import com.example.mvc_with_jpa_1.domain.Comment;
import com.example.mvc_with_jpa_1.domain.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@DataJpaTest
// https://0soo.tistory.com/40
// https://hoons-dev.tistory.com/134
// 직접 설정한 test 관련 yml 설정을 적용하고 싶다면 추가해야 한다
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// https://jun27.tistory.com/69
// https://velog.io/@choicore/Spring-DDD%EC%97%90%EC%84%9C-Repository-Test-%ED%95%98%EA%B8%B0
// @Repository 어노테이션이 붙은 CommentEmRepository 을 component scan 을 하지 못하는 (bean 으로 등록하지 못하는) 에러 해결
@ContextConfiguration(classes = CommentEmRepositoryTestConfig.class)
public class CommentEmRepositoryTest {

    @Autowired CommentRepository commentRepository;
    @Autowired CommentEmRepository commentEmRepository;
    @Autowired PostRepository postRepository;

    @BeforeEach
    void setUp() {
        Post mockPost = Post.builder().id(1L).title("title").content("content").build();
        Comment mockComment1 = Comment.toEntity(1L, "content", mockPost);
        Comment mockComment2 = Comment.toEntity(2L, "content2", mockPost);

        postRepository.save(mockPost);
        commentRepository.save(mockComment1);
        commentRepository.save(mockComment2);
    }

    @Test
    @DisplayName("fetch join 을 통한 paging")
    void findAllJoinFetchLimitByEm() {
        Long postId = 1L;
        PageRequest request = PageRequest.of(0, 1);
        List<Comment> commentList = commentEmRepository.findAllJoinFetchLimitByEm(postId, request);
//        System.out.println(commentList);
    }
}

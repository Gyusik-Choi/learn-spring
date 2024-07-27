package com.example.mvc_with_jpa_1.service;

import com.example.mvc_with_jpa_1.domain.Comment;
import com.example.mvc_with_jpa_1.domain.Post;
import com.example.mvc_with_jpa_1.dto.CommentResponse;
import com.example.mvc_with_jpa_1.repository.CommentEmRepository;
import com.example.mvc_with_jpa_1.repository.CommentRepository;
import com.example.mvc_with_jpa_1.repository.PostRepository;
import org.apache.coyote.BadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

// https://cjred.net/2020-04-30-junit-5-runwith-extendwith/
// https://www.baeldung.com/junit-5-runwith
@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    CommentService service;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentEmRepository commentEmRepository;

    @Test
    @DisplayName("모든 Comment 를 조회한다")
    void findAllComments() {
        // given
        Post mockPost = Post.builder()
                .id(1L)
                .build();
        Comment mockComment = Comment.builder()
                .id(1L)
                .post(mockPost)
                .content("content")
                .build();
        List<Comment> mockComments = List.of(mockComment);

        // https://galid1.tistory.com/772
        // https://www.nextree.io/mockito/
        given(commentRepository.findAllCommentWithPostFetchJoin())
                .willReturn(mockComments);

        // when
        List<CommentResponse> comments = service.findAllComments();

        // then
        assertThat(comments.get(0).getId()).isEqualTo(mockComment.getId());
    }

    @Test
    @DisplayName("id 를 기준으로 Comment 를 조회한다")
    void findComment() throws BadRequestException {
        // given
        Long id = 1L;
        Post mockPost = Post.builder()
                .id(id)
                .build();
        Comment mockComment = Comment.builder()
                .id(id)
                .post(mockPost)
                .content("content")
                .build();

        given(commentRepository.findById(id))
                .willReturn(Optional.ofNullable(mockComment));

        // when
        CommentResponse comment = service.findComment(id);

        // then
        assertThat(comment.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("id 가 일치하는 Comment 가 없으면 BadRequestException 이 발생한다")
    void findComment_2() throws BadRequestException {
        // given
        Long id = 1L;
        Post mockPost = Post.builder()
                .id(id)
                .build();
        Comment mockComment = Comment.builder()
                .id(id)
                .post(mockPost)
                .content("content")
                .build();

        given(commentRepository.findById(id))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> service.findComment(id))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("repository 에서 조회된 갯수만큼 반환된다")
    void findAllCommentPaging() {
        // given
        Long postId = 3L;
        PageRequest request = PageRequest.of(0, 1);

        Post mockPost = Post.builder()
                .id(postId)
                .build();
        Comment mockComment1 = Comment.builder()
                .id(1L)
                .post(mockPost)
                .content("content1")
                .build();
        Comment mockComment2 = Comment.builder()
                .id(2L)
                .post(mockPost)
                .content("content2")
                .build();
        List<Comment> comment = List.of(mockComment1, mockComment2);

        given(commentEmRepository.findAllJoinFetchLimitByEm(postId, request))
                .willReturn(comment);

        // when
        List<CommentResponse> result = service.findAllCommentPaging(postId, request);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getPostId()).isEqualTo(postId);
        assertThat(result.get(1).getPostId()).isEqualTo(postId);
    }
}

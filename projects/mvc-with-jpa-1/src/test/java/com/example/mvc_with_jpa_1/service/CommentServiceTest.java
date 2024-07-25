package com.example.mvc_with_jpa_1.service;

import com.example.mvc_with_jpa_1.domain.Comment;
import com.example.mvc_with_jpa_1.domain.Post;
import com.example.mvc_with_jpa_1.dto.CommentResponse;
import com.example.mvc_with_jpa_1.repository.CommentEmRepository;
import com.example.mvc_with_jpa_1.repository.CommentRepository;
import com.example.mvc_with_jpa_1.repository.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
    void findAllComments() {
        // given
        Comment mockComment = Comment
                .builder()
                .id(1L)
                .post(Post.builder().id(1L).build()).content("content")
                .build();
        List<Comment> mockComments = List.of(mockComment);

        // https://galid1.tistory.com/772
        // https://www.nextree.io/mockito/
        given(commentRepository.findAllCommentWithPostFetchJoin())
                .willReturn(mockComments);

        // when
        List<CommentResponse> comments = service.findAllComments();

        // then
        Assertions.assertThat(comments.get(0).getId()).isEqualTo(mockComment.getId());
    }
}

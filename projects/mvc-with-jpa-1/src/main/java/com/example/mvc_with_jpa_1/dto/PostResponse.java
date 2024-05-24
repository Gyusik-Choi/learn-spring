package com.example.mvc_with_jpa_1.dto;

import com.example.mvc_with_jpa_1.domain.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final List<CommentResponse> comments;

    @Builder
    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.comments = post.getComments()
                .stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }
}

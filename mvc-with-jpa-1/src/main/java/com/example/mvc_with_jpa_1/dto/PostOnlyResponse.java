package com.example.mvc_with_jpa_1.dto;

import com.example.mvc_with_jpa_1.domain.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostOnlyResponse {
    private Long id;
    private String title;
    private String content;

    @Builder
    public PostOnlyResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
    }
}

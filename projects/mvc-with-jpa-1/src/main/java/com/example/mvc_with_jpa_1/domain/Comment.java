package com.example.mvc_with_jpa_1.domain;

import com.example.mvc_with_jpa_1.dto.CommentSaveRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(length = 200, nullable = false)
    private String content;

    @Builder
    public Comment(Long id, Post post, String content) {
        this.id = id;
        this.post = post;
        this.content = content;
    }

    public static Comment toEntity(CommentSaveRequest saveRequest, Post post) {
        return Comment
                .builder()
                .content(saveRequest.getContent())
                .post(post)
                .build();
    }

    public static Comment toEntity(String content, Post mockPost) {
        return Comment.builder()
                .content(content)
                .post(mockPost)
                .build();
    }
}

package com.example.mvc_with_jpa_1.domain;

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
}

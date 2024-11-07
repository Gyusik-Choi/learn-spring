package com.example.mvc_with_jpa_1.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Getter
// https://kmhan.tistory.com/679
// findPost 에서 위의 에러 발생
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 200, nullable = false)
    private String content;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Attachment> attachments = new ArrayList<>();

    // https://aamoos.tistory.com/687
    @Builder
    public Post(Long id, String title, String content, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.comments = comments;
    }
}

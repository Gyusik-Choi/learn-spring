package com.example.mvc_with_jpa_1.dto;

import com.example.mvc_with_jpa_1.domain.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentResponse {

    private final Long id;
//    private final Post post;
    private final String content;

    @Builder
    CommentResponse(Comment comment) {
        this.id = comment.getId();
//        post 와 comment 는 1:N 관계인데
//        post 에서 comment 를 조회하는데
//        comment 에서 다시 post 를 조회하면서
//        infinite recursion 에러가 발생한다
//        -> "Could not write JSON: Infinite recursion (StackOverflowError)] with root cause"
//        그래서 아래의 코드를 사용하지 않는다
//        this.post = comment.getPost();
        this.content = comment.getContent();
    }
}

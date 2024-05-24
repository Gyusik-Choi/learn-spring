package com.example.mvc_with_jpa_1.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CommentSaveRequest {

//    https://www.inflearn.com/questions/16953/11-16-%EC%A7%88%EB%AC%B8%EC%9E%85%EB%8B%88%EB%8B%A4
//    @NotEmpty
    @NotNull
    private Long postId;

    @NotEmpty
    private String content;
}

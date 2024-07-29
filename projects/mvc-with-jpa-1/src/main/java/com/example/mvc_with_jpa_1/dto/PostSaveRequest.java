package com.example.mvc_with_jpa_1.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostSaveRequest {

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    // https://azurealstn.tistory.com/74
    PostSaveRequest() {}

    @Builder
    PostSaveRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static PostSaveRequest toDTO(String title, String content) {
        return PostSaveRequest
                .builder()
                .title("title")
                .content("content")
                .build();
    }
}

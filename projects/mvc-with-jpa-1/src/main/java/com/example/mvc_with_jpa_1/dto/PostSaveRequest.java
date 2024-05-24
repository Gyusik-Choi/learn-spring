package com.example.mvc_with_jpa_1.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class PostSaveRequest {

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;
}

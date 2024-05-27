package com.example.mvc_with_jpa_1.controller;

import com.example.mvc_with_jpa_1.dto.PostResponse;
import com.example.mvc_with_jpa_1.dto.PostSaveRequest;
import com.example.mvc_with_jpa_1.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public void findAll() {
        postService.findAll();
    }

    @GetMapping("/comment")
    public List<PostResponse> findAllPostWithComment() {
        return postService.findAllPostWithComment();
    }

    @GetMapping("/comment-attachment")
    public void findAllPostWithCommentAndAttachment() {
        postService.findAllPostWithCommentAndAttachment();
    }

    @GetMapping("/paging")
    public List<PostResponse> findAllPostPaging() {
        return postService.findAllPostPaging();
    }

    @GetMapping("/{id}")
    public PostResponse findPost(@PathVariable("id") Long id) throws BadRequestException {
        return postService.findPost(id);
    }

    @PostMapping()
    public void savePost(@RequestBody @Valid PostSaveRequest saveRequest) {
        postService.savePost(saveRequest);
    }

}

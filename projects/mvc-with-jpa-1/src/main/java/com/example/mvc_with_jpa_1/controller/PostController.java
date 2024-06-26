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
    public List<PostResponse> findAllPost() {
        return postService.findAllPost();
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
    // https://mangkyu.tistory.com/174
    // https://tecoble.techcourse.co.kr/post/2020-09-20-validation-in-spring-boot/
    public void savePost(@RequestBody @Valid PostSaveRequest saveRequest) {
        postService.savePost(saveRequest);
    }

}

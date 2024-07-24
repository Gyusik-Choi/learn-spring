package com.example.mvc_with_jpa_1.controller;

import com.example.mvc_with_jpa_1.dto.CommentResponse;
import com.example.mvc_with_jpa_1.dto.CommentSaveRequest;
import com.example.mvc_with_jpa_1.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @GetMapping()
    public List<CommentResponse> findAllComments() {
        return commentService.findAllComments();
    }

    @GetMapping("/{id}")
    public CommentResponse findComment(@PathVariable("id") Long id) throws BadRequestException {
        return commentService.findComment(id);
    }

//     https://oxylabs.io/blog/curl-get-requests
//     cURL -G http://localhost:8080 -d "page=1" -d "size=3"
//     cURL -X GET 으로 하면 -d 가 무시돼서 URL 에 parameter 를 담을 수 없다
//
//     https://www.baeldung.com/rest-api-pagination-in-spring
//     https://www.baeldung.com/spring-request-param
//     https://youngjinmo.github.io/2021/01/spring-request-parameter/
//    @GetMapping(params = {"page", "size"})
//    public void findAllCommentPaging(@RequestParam("page") int page, @RequestParam("size") int size) {
//        commentService.findAllCommentPaging(page, size);
//    }
    @GetMapping("/paging")
    public List<CommentResponse> findAllCommentPaging(@RequestParam("postId") int postId, Pageable pageable) {
        return commentService.findAllCommentPaging(postId, pageable);
    }

    @PostMapping()
    public void saveComment(@RequestBody @Valid CommentSaveRequest saveRequest) {
        commentService.saveComment(saveRequest);
    }
}

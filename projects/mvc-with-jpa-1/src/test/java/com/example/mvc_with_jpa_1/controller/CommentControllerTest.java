package com.example.mvc_with_jpa_1.controller;

import com.example.mvc_with_jpa_1.domain.Comment;
import com.example.mvc_with_jpa_1.domain.Post;
import com.example.mvc_with_jpa_1.dto.CommentResponse;
import com.example.mvc_with_jpa_1.dto.CommentSaveRequest;
import com.example.mvc_with_jpa_1.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentController controller;

    @MockBean
    private CommentService service;

    @Test
    void findAllComments() throws Exception {
        Long id = 1L;
        Comment mockComment = Comment
                .builder()
                .id(id)
                .content("content")
                .post(Post.builder().id(1L).build())
                .build();
        List<CommentResponse> mockResult = List.of(new CommentResponse(mockComment));
        Mockito.when(service.findAllComments()).thenReturn(mockResult);

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/comment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @DisplayName("path variable 에 정수를 보내지 않으면 에러가 발생한다")
    void findComment() throws Exception {
        String wrongPath = "id";
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get(String.format("/comment/%s", wrongPath))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("query parameter 에 postId 를 넣지 않으면 에러가 발생한다")
    void findAllCommentPaging() throws Exception {
        PageRequest request = PageRequest.of(0, 3);

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/comment/paging")
                        .queryParam("page", Integer.toString(request.getPageNumber()))
                        .queryParam("size", Integer.toString((int) request.getOffset()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("query parameter 에 postId 와 Pageable 을 모두 넣어야 한다")
    void findAllCommentPaging_2() throws Exception {
        PageRequest request = PageRequest.of(0, 3);

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/comment/paging")
                        .queryParam("postId", "1")
                        .queryParam("page", Integer.toString(request.getPageNumber()))
                        .queryParam("size", Integer.toString((int) request.getOffset()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Comment 를 저장한다")
    void saveComment() throws Exception {
        final CommentSaveRequest saveRequest = new CommentSaveRequest(1L, "content");
        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/comment")
                        // HttpMessageConversionException: Type definition error: simple type ~~
                        // 위와 같은 에러가 발생해서
                        // CommentSaveRequest 에 @NoArgsConstructor 를 추가해서 해결
                        // Jackson 의 ObjectMapper 가 json 타입으로 변환하기 위해서는 객체의 기본 생성자가 필요
                        .content(objectMapper.writeValueAsString(saveRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }
}

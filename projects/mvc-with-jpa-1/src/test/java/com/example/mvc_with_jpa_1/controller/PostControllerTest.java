package com.example.mvc_with_jpa_1.controller;

import com.example.mvc_with_jpa_1.domain.Post;
import com.example.mvc_with_jpa_1.dto.PostResponse;
import com.example.mvc_with_jpa_1.dto.PostSaveRequest;
import com.example.mvc_with_jpa_1.service.PostService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostController controller;

    @MockBean
    private PostService service;

    @Test
    @DisplayName("모든 Post 를 조회한다")
    void findAllPost() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/post")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @DisplayName("모든 Post 의 갯수는 1개고, 이를 정상적으로 조회한다")
    void findAllPost_2() throws Exception {
        Post mockPost = new Post(1L, "title", "content", List.of());
        Mockito.when(service.findAllPost()).thenReturn(List.of(new PostResponse(mockPost)));

        // https://g-db.tistory.com/entry/Spring-Test-MockMvc-Response%EB%A1%9C-%EA%B2%80%EC%A6%9D%ED%95%98%EA%B8%B0
        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/post")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();

        List<PostResponse> result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<PostResponse>>() {});
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(List.of(new PostResponse(mockPost)));
    }

    @Test
    @DisplayName("request body 에 title, content 가 필요한데 title 만 담아서 에러가 발생한다")
    void savePost() throws Exception {
        final PostSaveRequest mockPostSaveRequest = PostSaveRequest
                .builder()
                .title("title")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/post")
                        .content(objectMapper.writeValueAsString(mockPostSaveRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("request body 에 title, content 가 필요한데 content 만 담아서 에러가 발생한다")
    void savePost_2() throws Exception {
        final PostSaveRequest mockPostSaveRequest = PostSaveRequest
                .builder()
                .content("content")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/post")
                        .content(objectMapper.writeValueAsString(mockPostSaveRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("request body 에 title, content 을 모두 담아서 정상 동작 한다")
    void savePost_3() throws Exception {
        final PostSaveRequest mockPostSaveRequest = PostSaveRequest.toDTO("title", "content");

        ObjectMapper objectMapper = new ObjectMapper();

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/post")
                        .content(objectMapper.writeValueAsString(mockPostSaveRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    void findPostPaging() throws Exception {
        PageRequest request = PageRequest.of(0, 3);

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/post/paging")
                        .queryParam("page", Integer.toString(request.getPageNumber()))
                        .queryParam("size", Integer.toString((int) request.getOffset()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @DisplayName("path variable 에 정수를 보내지 않으면 에러가 발생한다")
    void findPost() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get(String.format("/post/%s", "s"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("path variable 에 0이하의 값을 보내면 에러가 발생한다")
    void findPost_2() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get(String.format("/post/%s", 0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("path variable 에 0이하의 값을 보내면 에러가 발생한다")
    void findPost_3() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get(String.format("/post/%s", -1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}

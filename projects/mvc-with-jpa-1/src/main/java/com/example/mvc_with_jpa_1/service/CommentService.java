package com.example.mvc_with_jpa_1.service;

import com.example.mvc_with_jpa_1.domain.Comment;
import com.example.mvc_with_jpa_1.domain.Post;
import com.example.mvc_with_jpa_1.dto.CommentSaveRequest;
import com.example.mvc_with_jpa_1.repository.CommentEmRepository;
import com.example.mvc_with_jpa_1.repository.CommentRepository;
import com.example.mvc_with_jpa_1.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentEmRepository commentEmRepository;

    public void findAllComments() {
        commentRepository.findAll();
    }

    public void findComment() {

    }

//    https://tecoble.techcourse.co.kr/post/2020-10-21-jpa-fetch-join-paging/
//    https://velog.io/@jinyoungchoi95/JPA-%EB%AA%A8%EB%93%A0-N1-%EB%B0%9C%EC%83%9D-%EC%BC%80%EC%9D%B4%EC%8A%A4%EA%B3%BC-%ED%95%B4%EA%B2%B0%EC%B1%85
//    N:1 에서는 fetch join 시
//    jpql 로 paging 쿼리 가능
//    Hibernate:
//        select
//            c1_0.id,
//            c1_0.content,
//            c1_0.post_id,
//            p1_0.id,
//            p1_0.content,
//            p1_0.title
//        from
//            comment c1_0
//        join
//            post p1_0
//                on p1_0.id=c1_0.post_id
//        limit
//            ?, ?
    public void findAllCommentPaging(int page, int size) {
        List<Comment> comments = commentEmRepository.findAllJoinFetchLimitByEm(page, size);
    }

    public void saveComment(CommentSaveRequest saveRequest) {
//        https://jgrammer.tistory.com/entry/JPA-%EB%B9%84%ED%9A%A8%EC%9C%A8%EC%A0%81%EC%9D%B8-%EC%97%B0%EA%B4%80-%EA%B4%80%EA%B3%84-%EC%A0%80%EC%9E%A5-%EB%B0%A9%EC%8B%9D-%EA%B0%9C%EC%84%A0%EB%B6%88%ED%95%84%EC%9A%94%ED%95%9C-select%EB%AC%B8-%EC%A0%9C%EA%B1%B0
        Post post = postRepository.getReferenceById(saveRequest.getPostId());
        Comment comment = Comment
                .builder()
                .post(post)
                .content(saveRequest.getContent())
                .build();
        commentRepository.save(comment);
    }
}

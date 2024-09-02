package com.example.mvc_with_jpa_1.service;

import com.example.mvc_with_jpa_1.domain.Comment;
import com.example.mvc_with_jpa_1.domain.Post;
import com.example.mvc_with_jpa_1.dto.CommentResponse;
import com.example.mvc_with_jpa_1.dto.CommentSaveRequest;
import com.example.mvc_with_jpa_1.repository.CommentEmRepository;
import com.example.mvc_with_jpa_1.repository.CommentRepository;
import com.example.mvc_with_jpa_1.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentEmRepository commentEmRepository;

//     Comment 의 @ManyToOne 로 걸린 post 필드는
//     default fetch 가 FetchType.EAGER 라서
//     N + 1 쿼리를 방지하기 위해 FetchType.LAZY 로 변경한다
    public List<CommentResponse> findAllComments() {
//        Spring Data JPA 의 findAll 로 조회할 때
//        batch size 를 설정하면
//        Lazy Loading 이 걸린 Post 의 속성을 조회할 때 (외래키인 id 제외)
//        batch size 만큼의 추가 쿼리가 발생한다
//        batch size 를 설정하지 않으면
//        Lazy Loading 이 걸린 Post 의 속성을 조회할 때 (외래키인 id 제외)
//        N + 1 쿼리 발생
//        List<Comment> comments = commentRepository.findAll();
//        for (Comment comment : comments) {
//            Post post = comment.getPost();
//            System.out.println(post.getTitle());
//        }

//        fetch join 을 걸면 (inner) join 쿼리가 발생하면서 한번의 쿼리로 조회 가능
//        Hibernate:
//            select
//                c1_0.id,
//                c1_0.content,
//                c1_0.post_id,
//                p1_0.id,
//                p1_0.content,
//                p1_0.title
//            from
//                comment c1_0
//            join
//                post p1_0
//            on p1_0.id=c1_0.post_id
//        List<Comment> comments = commentRepository.findAllCommentWithPostFetchJoin();
//        for (Comment comment : comments) {
//            Post post = comment.getPost();
//            System.out.println(post.getTitle());
//        }

//        List<Comment> comments = commentEmRepository.findAll();
//        for (Comment comment : comments) {
//            Post post = comment.getPost();
//            System.out.println(post.getTitle());
//        }

        List<Comment> comments = commentEmRepository.findAllJoinFetch();
        for (Comment comment : comments) {
            Post post = comment.getPost();
            System.out.println(post.getTitle());
        }
        return comments
                .stream()
                .map(CommentResponse::toDTO)
                .collect(Collectors.toList());
    }

    public CommentResponse findComment(Long id) throws BadRequestException {
//        Comment comment = commentRepository
//                .findById(id)
//                .orElseThrow(BadRequestException::new);

//        Comment comment = commentRepository
//                .findByCommentId(id)
//                .orElseThrow(BadRequestException::new);

//        Comment comment = commentEmRepository
//                .findById(id);

        Comment comment = commentEmRepository
                .findByIdJoinFetch(id);

        System.out.println(comment.getPost().getTitle());

        return CommentResponse.toDTO(comment);
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
    public List<CommentResponse> findAllCommentPaging(Long postId, Pageable pageable) {
        List<Comment> comments = commentEmRepository.findAllJoinFetchLimitByEm(postId, pageable);

        return comments
                .stream()
                .map(CommentResponse::toDTO)
                .collect(Collectors.toList());
    }

    public void saveComment(CommentSaveRequest saveRequest) {
//        https://jgrammer.tistory.com/entry/JPA-%EB%B9%84%ED%9A%A8%EC%9C%A8%EC%A0%81%EC%9D%B8-%EC%97%B0%EA%B4%80-%EA%B4%80%EA%B3%84-%EC%A0%80%EC%9E%A5-%EB%B0%A9%EC%8B%9D-%EA%B0%9C%EC%84%A0%EB%B6%88%ED%95%84%EC%9A%94%ED%95%9C-select%EB%AC%B8-%EC%A0%9C%EA%B1%B0
        Post post = postRepository.getReferenceById(saveRequest.getPostId());
        Comment comment = Comment.toEntity(saveRequest, post);
        commentRepository.save(comment);
    }
}

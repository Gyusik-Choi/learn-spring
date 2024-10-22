package com.example.mvc_with_jpa_1.dto;

import com.example.mvc_with_jpa_1.domain.Comment;
import com.example.mvc_with_jpa_1.domain.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentResponse {

    private final Long id;
//    private final Post post;
//    private final PostOnlyResponse post;
    private final Long postId;
//    private final String postTitle;
    private final String content;

    public static CommentResponse toDTO(Comment comment) {
        return CommentResponse
                .builder()
                .comment(comment)
                .build();
    }

    @Builder
    public CommentResponse(Comment comment) {
        this.id = comment.getId();
//        post 와 comment 는 1:N 관계인데
//        post 에서 comment 를 조회하는데
//        comment 에서 다시 post 를 조회하면서
//        infinite recursion 에러가 발생한다
//        -> "Could not write JSON: Infinite recursion (StackOverflowError)] with root cause"
//        그래서 아래의 코드를 사용하지 않는다
//        this.post = comment.getPost();
//
//        https://pasudo123.tistory.com/350
//        위의 에러는 양방향 관계의 엔티티를 컨트롤러에서 Jackson 라이브러리를 이용해서
//        JSON 타입으로 변환하는 과정에서 발생한다
//        CommentResponse 객체 자체는 별도의 DTO 지만 필드인 post 는 엔티티다
//        JSON 타입을 변환하는 과정에서 Post 는 Comment 를 참조하고 Comment 는 Post 를 참조하는 과정이
//        무한 반복되면서 무한 루프가 일어난다
//        필드 post 를 DTO 로 변환해서 에러를 해결할 수 있다
//        DTO 로 변환하면서 Post 에서 필드로 갖는 comments 를 제거했다
//        this.post = PostOnlyResponse.builder().post(comment.getPost()).build();

//        또 다른 방법으로는
//        N:1 에서 1의 정보를 모두 전달할 필요가 없으면
//        Post 의 일부 필드만 반환할 수도 있다
        this.postId = comment.getPost().getId();

//        paging 의 경우
//        fetch join 이 아닌 join 으로 조회시
//        postId 가 아닌 postTitle 에 접근하면 N + 1 쿼리가 발생한다
//        this.postTitle = comment.getPost().getTitle();

        this.content = comment.getContent();
    }
}

package com.example.mvc_with_jpa_1.service;

import com.example.mvc_with_jpa_1.domain.Attachment;
import com.example.mvc_with_jpa_1.domain.Comment;
import com.example.mvc_with_jpa_1.domain.Post;
import com.example.mvc_with_jpa_1.dto.PostResponse;
import com.example.mvc_with_jpa_1.dto.PostSaveRequest;
import com.example.mvc_with_jpa_1.repository.PostEmRepository;
import com.example.mvc_with_jpa_1.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    public final PostRepository postRepository;
    public final PostEmRepository postEmRepository;

    public List<PostResponse> findAllPost() {
//        Post 엔티티에서 일대다 연관관계를 맺는
//        Comment 엔티티에 대한 fetch 조건을
//        Eager, Lazy 어떤 것으로 하더라도
//        N + 1 발생
//
//        좀 더 엄밀히 말하자면,
//        Lazy 로 fetch type 을 설정했을 경우
//        findAll 만으로 N + 1 쿼리가 발생하지는 않고
//        post 의 연관관계로 있는
//        comment, attachment 의 속성을 조회할 때
//        쿼리가 발생한다
        List<Post> posts = postRepository.findAll();

        List<String> commentContents = posts
                .stream()
                .map(Post::getComments)
                .flatMap(Collection::stream)
                .map(Comment::getContent)
                .toList();

        List<String> attachmentFilenames = posts
                .stream()
                .map(Post::getAttachments)
                .flatMap(Collection::stream)
                .map(Attachment::getFilename)
                .toList();

        return posts
                .stream()
                .map((p) -> PostResponse
                        .builder()
                        .post(p)
                        .build())
                .collect(Collectors.toList());
    }

    public List<PostResponse> findAllPostWithComment() {
//        Post 엔티티에서 일대다 연관관계를 맺는
//        Comment 엔티티에 대한 fetch 조건을
//        Eager, Lazy 어떤 것으로 하더라도
//        N + 1 발생
//        List<Post> posts = postRepository.findAll();

//        N + 1 발생
//        List<Post> posts = postEmRepository.findAll();

//        https://jojoldu.tistory.com/165
//        N + 1 대안 - 1
//        List<Post> posts = postRepository.findAllPostWithCommentJoinFetch();

//        N + 1 대안 - 2
//        List<Post> posts = postRepository.findAllEntityGraph();

//        ???
//        대안 1, 2 둘 다 예상과는 다르게 distinct 를 쓰지 않아도
//        부모 엔티티가 중복되서 나타나지 않는다
//        ???

//        N + 1 대안 - 3
//        Spring Data JPA 가 아니라
//        EntityManager 를 직접 사용해서 조회하면 중복 문제가 나타나는지 해보았으나
//        마찬가지로 중복되지 않는다
        List<Post> posts = postEmRepository.findAllJoinFetchByEm();

//        https://delvering.tistory.com/52
//        이분 덕분에 해결할 수 있었다
//        hibernate 6 부터 자동으로 중복되는 부모 엔티티를 제거해준다
//        현재 프로젝트에서 6.4.1 을 사용하고 있어서
//        distinct 를 쓰지 않아도 부모 엔티티 중복 문제가 발생하지 않았다
//        https://www.inflearn.com/questions/911481/hibernate-6-%EB%B6%80%ED%84%B0%EB%8A%94-%ED%95%AD%EC%83%81-distinct-%EA%B0%80-%EC%A0%81%EC%9A%A9%EB%90%9C%EB%8B%A4%EA%B3%A0-%ED%95%A9%EB%8B%88%EB%8B%A4-%EB%94%B0%EB%9D%BC%ED%96%88%EB%8A%94%EB%8D%B0-%EA%B2%B0%EA%B3%BC%EA%B0%80-%EB%8B%A4%EB%A5%B4%EA%B2%8C-%EB%82%98%EC%99%80%EC%84%9C-%ED%95%9C%EC%B0%B8-%ED%95%B4%EB%A7%B8%EB%84%A4%EC%9A%94
//        https://github.com/hibernate/hibernate-orm/blob/6.0/migration-guide.adoc#distinct

        for (Post p : posts) {
            System.out.println("post = " + p);
            for (Comment c : p.getComments()) {
                System.out.println(c.getContent());
            }
        }

        return posts
                .stream()
                .map((p) -> PostResponse
                        .builder()
                        .post(p)
                        .build())
                .collect(Collectors.toList());
    }

    public void findAllPostWithCommentAndAttachment() {
//        Post 의 comment, attachment 는 모두 1:N 관계로
//        fetch type 을 Lazy 로 걸어놨다
//        findAll 만으로는 comment, attachment 를 구하는 쿼리가 발생하지 않는다
//        postRepository.findAll();

//        MultipleBagFetchException 발생
//        List<Post> posts = postRepository.findAllPostWithCommentAndAttachmentJoinFetch();

//        batch size 로 10 을 적용
//        Post 를 comment, attachment 를 join 하는 쿼리 이후
//        comment 를 in 쿼리로 조회하는 쿼리
//        attachment 를 in 쿼리로 조회하는 쿼리
//        총 3개 쿼리 발생
//        Hibernate:
//            select
//                p1_0.id,
//                p1_0.content,
//                p1_0.title
//            from
//                post p1_0
//            join
//                comment c1_0
//                    on p1_0.id=c1_0.post_id
//            join
//                attachment a1_0
//                    on p1_0.id=a1_0.post_id
//
//        Hibernate:
//            select
//                c1_0.post_id,
//                c1_0.id,
//                c1_0.content
//            from
//                comment c1_0
//            where
//                c1_0.post_id in (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
//
//        Hibernate:
//            select
//                a1_0.post_id,
//                a1_0.id,
//                a1_0.filename
//            from
//                attachment a1_0
//            where
//                a1_0.post_id in (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        List<Post> posts = postRepository.findAllPostWithCommentAndAttachmentNoJoinFetch();

        List<String> commentContents = posts
                .stream()
                .map(Post::getComments)
                .flatMap(Collection::stream)
                .map(Comment::getContent)
                .toList();

        List<String> attachmentFilenames = posts
                .stream()
                .map(Post::getAttachments)
                .flatMap(Collection::stream)
                .map(Attachment::getFilename)
                .toList();
    }

    public List<PostResponse> findPostPaging(Pageable pageable) {
//        List<Post> posts = postRepository.findAllJoinFetch();
//        List<Post> posts = postEmRepository.findAllJoinFetchLimitByEm(pageable);
        List<Post> posts = postEmRepository.findAllJoinNoFetchLimitByEm(pageable);

        for (Post p : posts) {
            System.out.println("post = " + p);
            for (Comment c : p.getComments()) {
                System.out.println(c.getContent());
            }
        }

        return posts
                .stream()
                .map((p) -> PostResponse
                        .builder()
                        .post(p)
                        .build())
                .collect(Collectors.toList());
    }

    public PostResponse findPost(Long id) throws BadRequestException {
        Post post = postRepository
                .findById(id)
                .orElseThrow(BadRequestException::new);

        return PostResponse
                .builder()
                .post(post)
                .build();
    }

    public void savePost(PostSaveRequest saveRequest) {
        Post post = Post
                .builder()
                .title(saveRequest.getTitle())
                .content(saveRequest.getContent())
                .build();
        postRepository.save(post);
    }
}

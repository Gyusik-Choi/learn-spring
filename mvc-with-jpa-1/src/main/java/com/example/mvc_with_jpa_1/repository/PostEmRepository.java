package com.example.mvc_with_jpa_1.repository;

import com.example.mvc_with_jpa_1.domain.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostEmRepository {

    private final EntityManager em;

    public List<Post> findAll() {
        return em.createQuery("select p from Post p", Post.class)
                .getResultList();
    }

//    https://onejunu.tistory.com/35
    public List<Post> findAllJoinFetch() {
        String query = "select p from Post p join fetch p.comments";
        return em.createQuery(query, Post.class).getResultList();
    }

//    https://tecoble.techcourse.co.kr/post/2020-10-21-jpa-fetch-join-paging/
//    1:N 관계에서
//    1에서 N을 fetch join 으로 paging 조회하는 경우
//
//    offset, limit 이 쿼리에 적용되는게 아니다
//    createQuery 에 있는 쿼리문을 그대로 실행한 후에
//    서버에서 쿼리 결과 값을 메모리에 쌓은 후
//    paging 을 처리하는 방식이다
//    데이터가 많은 경우 메모리 부하가 커질 수 있다
//
//    아예 경고 로그가 (SQL 로그 이전에) 발생한다
//    HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
//
//    SQL 로그를 보면 아래처럼 offset, limit 등은 보이지 않는다
//    Hibernate:
//    select
//        p1_0.id,
//        c1_0.post_id,
//        c1_0.id,
//        c1_0.content,
//        p1_0.content,
//        p1_0.title
//    from
//        post p1_0
//    join
//        comment c1_0
//            on p1_0.id=c1_0.post_id
    public List<Post> findAllJoinFetchLimit(Pageable pageable) {
        TypedQuery<Post> query = em.createQuery("select p from Post p join fetch p.comments", Post.class);
        query.setFirstResult(pageable.getPageNumber());
        query.setMaxResults(pageable.getPageSize());
        return query.getResultList();
    }

//    https://velog.io/@hyungzin0309/JPA-%EC%9D%BC-%EB%8C%80-%EB%8B%A4-Fetch-Join-Pagination
//    batch size 를 application.properties 에 걸어야 한다
    public List<Post> findAllNoJoinFetchLimit(Pageable pageable) {
        TypedQuery<Post> query = em.createQuery("select p from Post p join p.comments c", Post.class);
//        !
//        주의할점은
//        1:N 에서 paging 을 걸었다고 해서
//        paging 갯수가 1에 걸린다고 생각하면 안 된다
//        실제 db 에 조회한다고 생각해보자
//
//        limit 을 3으로 건다고 가정하면
//        db 에는
//        post 가 1개가 있고
//        post 1개에 대한 외래키를 갖는
//        comment 가 5개라고 하면
//        post 1개에 대한 comment 가 3개가 조회된다
//        ->
//        post 1, comment 1
//        post 1, comment 2
//        post 1, comment 3
//        !
        query.setFirstResult(pageable.getPageNumber());
        query.setMaxResults(pageable.getPageSize());
        return query.getResultList();
    }

    public Post findPostWithComment(Long postId) {
        return em.createQuery("select p from Post p where p.id = :id", Post.class)
                .setParameter("id", postId)
                .getSingleResult();
    }

    public Post findPostWithCommentJoinFetch(Long postId) {
        return em.createQuery("select p from Post p join fetch p.comments where p.id = :id", Post.class)
                .setParameter("id", postId)
                .getSingleResult();
    }

    public Post findPostWithCommentAndAttachmentNoJoinFetch(Long postId) {
        return em.createQuery("select p from Post p join p.comments join p.attachments where p.id = :id", Post.class)
                .setParameter("id", postId)
                .getSingleResult();
    }
}

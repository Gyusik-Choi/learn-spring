package com.example.mvc_with_jpa_1.repository;

import com.example.mvc_with_jpa_1.domain.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentEmRepository {

    private final EntityManager em;

    public List<Comment> findAll() {
        return em.createQuery("select c from Comment c", Comment.class)
                .getResultList();
    }

    public List<Comment> findAllJoinFetch() {
        return em.createQuery("select c From Comment c join fetch c.post", Comment.class)
                .getResultList();
    }

//    1:N 에서와 달리
//    N:1 관계에서는 fetch join 으로 paging 조회하는 경우
//    paging 이 정상적으로 작동한다
//    (MySQL 의 경우 paging 쿼리를 할 때
//    offset, limit 을 함께 사용할 수도 있고
//    limit 만 사용할 수도 있다
//    SQL 을 찍어보면 limit 만 나오고 offset 이 나오지 않을 수 있다)
//     Hibernate:
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
    public List<Comment> findAllJoinFetchLimitByEm(Long postId, Pageable pageable) {
//        https://velog.io/@j3beom/JPA-JPQL-Fetch-Join
//        https://www.inflearn.com/questions/15876/fetch-join-%EC%8B%9C-%EB%B3%84%EC%B9%AD%EA%B4%80%EB%A0%A8-%EC%A7%88%EB%AC%B8%EC%9E%85%EB%8B%88%EB%8B%A4
//        fetch join 대상에는 별칭을 줄 수 없으나 hibernate 에서만 가능
//        TypedQuery<Comment> query = entityManager.createQuery("select c from Comment c join fetch c.post p where p.id = :postId", Comment.class);
        TypedQuery<Comment> query = em.createQuery("select c from Comment c join fetch c.post where c.post.id = :postId", Comment.class);
        query.setParameter("postId", postId);
        query.setFirstResult(pageable.getPageNumber());
        query.setMaxResults(pageable.getPageSize());
        return query.getResultList();
    }

    public Comment findById(Long commentId) {
        return em.createQuery("select c from Comment c where c.id = :commentId", Comment.class)
                .setParameter("commentId", commentId)
                .getSingleResult();
    }

    public Comment findByIdJoinFetch(Long commentId) {
        return em.createQuery("select c from Comment c join fetch c.post where c.id = :commentId", Comment.class)
                .setParameter("commentId", commentId)
                .getSingleResult();
    }
}

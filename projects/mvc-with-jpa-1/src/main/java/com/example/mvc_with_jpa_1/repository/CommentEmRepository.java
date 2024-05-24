package com.example.mvc_with_jpa_1.repository;

import com.example.mvc_with_jpa_1.domain.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentEmRepository {

    private final EntityManager entityManager;

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
    public List<Comment> findAllJoinFetchLimitByEm(int page, int size) {
        TypedQuery<Comment> query = entityManager.createQuery("select c from Comment c join fetch c.post", Comment.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }
}

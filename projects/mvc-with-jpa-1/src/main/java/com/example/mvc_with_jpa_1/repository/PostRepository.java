package com.example.mvc_with_jpa_1.repository;

import com.example.mvc_with_jpa_1.domain.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from Post p join fetch p.comments")
    List<Post> findAllPostWithCommentJoinFetch();

//    MultipleBagFetchException 발생한다
//    org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags: [com.example.mvc1.domain.Post.attachments, com.example.mvc1.domain.Post.comments]
    @Query("select p from Post p join fetch p.comments join fetch p.attachments")
    List<Post> findAllPostWithCommentAndAttachmentJoinFetch();

    @Query("select p from Post p join p.comments c join p.attachments a")
    List<Post> findAllPostWithCommentAndAttachmentNoJoinFetch();

    // Post 엔티티에서 연관 관계로 설정한 변수명
    @EntityGraph(attributePaths = "comments")
    @Query("select p from Post p")
    List<Post> findAllEntityGraph();

    @Query("select p from Post p join fetch p.comments where p.id = :id")
    Post findPostWithComment(@Param("id") Long id);

//    https://tecoble.techcourse.co.kr/post/2020-10-21-jpa-fetch-join-paging/
//    JPQL 로 MySQL 의 방언에 속하는 LIMIT 절을 직접 사용할 수 없다
//    @Query("select p from Post p join fetch p.comments limit 3")
//    List<Post> findAllJoinFetchLimit();
}

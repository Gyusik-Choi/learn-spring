# Spring MVC with JPA - 1

스프링 MVC 를 기반으로 JPA 를 사용하면서 미니 프로젝트를 진행한다.

미니 프로젝트를 진행하면서 학습한 내용들을 정리할 예정이다.

<br>

정리할 내용들

- Controller & RestController
- RequiredArgsConstructor
- JPA
  - N + 1


  - Join 과 Fetch Join 의 차이
  - Paging
  - MultipleBagfetchException
  - DTO
- Querydsl 설정
- Testing

<br>

## Controller & RestController

[이곳](https://kuidoli.tistory.com/16) 에 별도로 정리했다.

<br>

## N + 1

[이곳](https://kuidoli.tistory.com/17) 에 별도로 정리했다.

<br>

## Join 과 Fetch Join 의 차이, Paging, MultipleBagFetchException, DTO

[이곳](https://kuidoli.tistory.com/18) 에 별도로 정리했다.

<br>

## Querydsl 설정

spring boot 3.0 이상과 3.0 미만 환경의 build.gradle 에서 Querydsl 을 설정하는 방법이 다르다.

[김영한님의 강의](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-db-2) 에서 설정한 방식을 그대로 적용하니 적용되지 않았다. 강의에서는 spring boot 2.x 버전을 사용하는데 이 프로젝트에서는 spring boot 3.x 버전을 사용해서 적용되지 않았다.

[이분](https://velog.io/@juhyeon1114/Spring-QueryDsl-gradle-%EC%84%A4%EC%A0%95-Spring-boot-3.0-%EC%9D%B4%EC%83%81)의 글을 참고해서 Querydsl 설정을 완료할 수 있었다. 이 글에서 build.gradle 코드가 2번 나오는데 2개 중 아래의 코드를 적용해야 한다.

<br>

```groovy
// annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jakarta"
annotationProcessor "com.querydsl:querydsl-apt:5.0.0:jakarta"
```

annotationProcessor 설정이 위, 아래가 다른데 그 중에서도 com.querydsl:querydsl-apt 설정이 다르다. 위에서 주석으로 처리한 방식을 적용하면 compileJava 를 run 할 경우 아래와 같은 에러가 발생한다.

```
Unable to load class 'javax.persistence.Entity'.
This is an unexpected error. Please file a bug containing the idea.log file.
```

<br>

## (Unit) Testing

### Controller

> [To test whether Spring MVC controllers are working as expected, use the `@WebMvcTest` annotation.](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html#testing.spring-boot-applications.spring-mvc-tests)

컨트롤러 테스트에는 @WebMvcTest 어노테이션을 활용했다. @SpringBootTest 어노테이션을 사용할 수도 있으나 Controller 만 빠르게 테스트하고 싶어서 @WebMvcTest 를 사용했다.

<br>

```
@Controller, @ControllerAdvice, @JsonComponent, Converter, GenericConverter, Filter, HandlerInterceptor, WebMvcConfigurer, WebMvcRegistrations, and HandlerMethodArgumentResolver.
```

@WebMvcTest 는 스캔하는 빈이 제한된다. @WebMvcTest 가 스캔하는 빈의 종류는 위와 같다. 

@Service 를 스캔하지 않기 때문에(@Component 도 스캔하지 않음) 컨트롤러에 서비스의 의존성이 있다면 @MockBean 로 mock 객체를 만들어서 의존성을 해결할 수 있다.

<br>

그러나 [이글](https://mangkyu.tistory.com/145)과 [또 다른 글](https://tech.kakaopay.com/post/mock-test-code/#mockbean-%EC%82%AC%EC%9A%A9-%EC%8B%9C-application-context-%EC%B4%88%EA%B8%B0%ED%99%94-%EB%AC%B8%EC%A0%9C)에서 언급하는 것처럼 @WebMvcTest, @MockBean 도 단점이 존재하기 때문에 이에 대한 학습이 추가로 필요하다.

<br>

### Service

<br>

### Repository

```
org.hibernate.exception.SQLGrammarException: could not prepare statement [Table "POST" not found (this database is empty); SQL statement:
```

h2 memory db 를 이용해서 테스트를 진행하려 했으나 테이블이 제대로 생성되지 않는 에러가 발생했다.

<br>

```sql
CREATE TABLE post (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    content VARCHAR(200) NOT NULL
);

CREATE TABLE comment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    content VARCHAR(200) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES post(id)
);
```

결론적으로 schema.sql 을 이용해서 테이블 생성 구문을 넣어서 해결할 수 있었다.

개발시에는 MySQL 을 사용했는데 수동으로 database 와 table 을 생성한 이후에 엔티티와 매핑했다. 테스트에서는 ddl-auto 옵션을 create-drop 으로해서 테스트마다 테이블을 자동으로 생성하고 제거하려 했으나 되지 않았다.

아직 정확한 원인은 잘 모르겠으나 개발시에 수동으로 엔티티와 매핑해서 테스트에서는 schema.sql 파일 없이 자동으로 테이블을 생성하지 못하는 듯 하다.

<br>

<참고>

https://kukim.tistory.com/150

https://velog.io/@juhyeon1114/Spring-QueryDsl-gradle-%EC%84%A4%EC%A0%95-Spring-boot-3.0-%EC%9D%B4%EC%83%81

https://jojoldu.tistory.com/165

https://velog.io/@jinyoungchoi95/JPA-%EB%AA%A8%EB%93%A0-N1-%EB%B0%9C%EC%83%9D-%EC%BC%80%EC%9D%B4%EC%8A%A4%EA%B3%BC-%ED%95%B4%EA%B2%B0%EC%B1%85

https://tecoble.techcourse.co.kr/post/2020-10-21-jpa-fetch-join-paging/

https://nosy-rabbit.tistory.com/entry/git-%EC%9D%B8%ED%85%94%EB%A6%AC%EC%A0%9C%EC%9D%B4-%EC%97%90%EC%84%9C-applicationproperties%ED%8C%8C%EC%9D%BC-gitignore-%ED%95%98%EA%B8%B0

https://tecoble.techcourse.co.kr/post/2020-09-20-validation-in-spring-boot/

https://mangkyu.tistory.com/174

https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-framework/server.html

https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html#testing.spring-boot-applications.spring-mvc-tests

https://mangkyu.tistory.com/145

https://tech.kakaopay.com/post/mock-test-code/#mockbean-%EC%82%AC%EC%9A%A9-%EC%8B%9C-application-context-%EC%B4%88%EA%B8%B0%ED%99%94-%EB%AC%B8%EC%A0%9C
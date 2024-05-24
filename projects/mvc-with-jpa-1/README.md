# Spring MVC with JPA - 1 (진행중)

스프링을 학습하면서 배운 내용들을 실습 해보기 위한 미니 프로젝트다.

스프링 MVC 를 기반으로 JPA 를 사용하면서 프로젝트를 진행한다.

JPA 를 사용하면서 마주할 수 있는 N + 1, fetch join 의 pagination, 1:N 관계에서 2개 이상의 자식 테이블의 fetch join 으로 인한 MultipleBagfetchException 문제 등을 다룰 예정이다.

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

## N + 1



<br>

## fetch join 과 pagination



<br>

## MultipleBagfetchException



<br>

<참고>

https://kukim.tistory.com/150

https://velog.io/@juhyeon1114/Spring-QueryDsl-gradle-%EC%84%A4%EC%A0%95-Spring-boot-3.0-%EC%9D%B4%EC%83%81
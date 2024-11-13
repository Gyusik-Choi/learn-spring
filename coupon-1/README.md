# Coupon-1

[패스트캠퍼스 강의](https://fastcampus.co.kr/dev_online_traffic_data) 에서 선착순 쿠폰 발급 강의를 정리하기 위한 프로젝트다. 강의에서는 멀티 모듈로 진행하는데 여기서는 싱글 모듈로 진행한다.

<br>

## 설정 에러 및 해결

### docker 명령어

- docker-compose.yml 실행

```
docker-compose up -d
```

<br>

- docker-compose.yml 을 구동할 worker 를 3개로 설정

```
docker-compose up -d --scale worker=3
```

<br>

### docker 에서 mysql 연결

- [mysql port 변경 방법](https://infinitecode.tistory.com/49)

```
// my.cnf
port=3308
```

<br>

- [Public key retrieval is not allowed 에러](https://deeplify.dev/database/troubleshoot/public-key-retrieval-is-not-allowed)

```
드라이버의 속성에서 allowPublickeyRetrieval 을 true 로 변경해야 한다
```

<br>

- [터미널로 port 변경방법 확인](https://seongeun-it.tistory.com/317)

```

```

<br>

- 계정 관련 에러

```
Access denied for user 'abcd'@'%' to database 'coupon-1'
```

port 변경 후 접근 에러 발생가 발생했다. docker-compose 를 이용하고 있었는데 volume 을 삭제하고 다시 실행시켜서 해결했다.

https://velog.io/@ppinkypeach/Docker-Compose%EB%A1%9C-Mysql-%EC%8B%A4%ED%96%89-%EC%8B%9CAccess-denied-for-user-rootlocalhost-using-passwordYES-%ED%95%B4%EA%B2%B0

https://kdh0518.tistory.com/entry/Docker-ERROR-1045-28000-Access-denied-for-user-rootlocalhost-%ED%95%B4%EA%B2%B0-%EB%B0%A9%EB%B2%95-%EC%B4%9D-%EC%A0%95%EB%A6%AC

<br>

### Spring

- component scan 에러

component scan 관련 에러가 있었는데 한참헤맨 후 active profile 설정을 다시 하니 해결이 됐다. 정확히 이것 때문에 해결이 된지는 모르겠다. CouponApiApplication 의 active profile 을 local 로 설정했다. 아래의 링크를 참고했다.

https://jojoldu.tistory.com/547

<br>

### Python

- 인터프리터 에러

```
No Python interpreter configured for the module
```

```
file - project structure 의 SDKs 에서 Python SDK 를 System Interpreter 로 설정한 뒤, file - project structure 의 Modules 에서 + 를 눌러서 Python 을 추가했다. load-test 폴더가 위치한 곳은 main, test 폴더 내부가 아니라 최상단 경로라 main, test 와 나란한 경로에 추가했다.
```

https://ddururiiiiiii.tistory.com/351

https://jojoldu.tistory.com/465




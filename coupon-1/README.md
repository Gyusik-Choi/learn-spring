



docker 명령어

docker-compose up -d

docker-compose up -d --scale worker=3



```
Can not read response from server. Expected to read 4 bytes, read 0 bytes before connection was unexpectedly lost.
```

docker 로 mysql 연결시 port 변경 방법

https://infinitecode.tistory.com/49



port 변경 후에도 에러가 발생하는 경우

https://seongeun-it.tistory.com/317



```
Access denied for user 'abcd'@'%' to database 'coupon-1'
```

port 변경 후 접근 에러 발생중...



docker-compose 를 이용하고 있었는데 volume 을 삭제하고 다시 실행시켜서 해결

https://velog.io/@ppinkypeach/Docker-Compose%EB%A1%9C-Mysql-%EC%8B%A4%ED%96%89-%EC%8B%9CAccess-denied-for-user-rootlocalhost-using-passwordYES-%ED%95%B4%EA%B2%B0

https://kdh0518.tistory.com/entry/Docker-ERROR-1045-28000-Access-denied-for-user-rootlocalhost-%ED%95%B4%EA%B2%B0-%EB%B0%A9%EB%B2%95-%EC%B4%9D-%EC%A0%95%EB%A6%AC





mysql 연결시 public key retrieval is not allowed 에러가 발생했을 경우 해결 방법

https://deeplify.dev/database/troubleshoot/public-key-retrieval-is-not-allowed



component scan 관련 에러가 있었는데 한참헤맨 후 active profile 설정을 다시 하니 해결이 됐다. 정확히 이것 때문에 해결이 된지는 모르겠다. CouponApiApplication 의 active profile 을 local 로 설정했다. 아래의 링크를 참고했다.

https://jojoldu.tistory.com/547



```
Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.

Reason: Failed to determine a suitable driver class
```

hikari jdbc 설정시 에러 해결 방법

https://bje0716.tistory.com/36
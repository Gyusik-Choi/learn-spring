-- 테이블 생성 및 기본 데이터
CREATE TABLE `coupons`
(
    `id`                   BIGINT(20) NOT NULL AUTO_INCREMENT,
    `title`                VARCHAR(255) NOT NULL COMMENT '쿠폰명',
    `coupon_type`          VARCHAR(255) NOT NULL COMMENT '쿠폰 타입 (선착순 쿠폰, ..)',
    `total_quantity`       INT NULL COMMENT '쿠폰 발급 최대 수량',
    `issued_quantity`      INT          NOT NULL COMMENT '발급된 쿠폰 수량',
    `discount_amount`      INT          NOT NULL COMMENT '할인 금액',
    `min_available_amount` INT          NOT NULL COMMENT '최소 사용 금액',
    `date_issue_start`     datetime(6) NOT NULL COMMENT '발급 시작 일시',
    `date_issue_end`       datetime(6) NOT NULL COMMENT '발급 종료 일시',
    `date_created`         datetime(6) NOT NULL COMMENT '생성 일시',
    `date_updated`         datetime(6) NOT NULL COMMENT '수정 일시',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
   DEFAULT CHARSET = utf8mb4
     COMMENT '쿠폰 정책';

CREATE TABLE `coupon_issues`
(
    `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
    `coupon_id`    BIGINT(20) NOT NULL COMMENT '쿠폰 ID',
    `user_id`      BIGINT(20) NOT NULL COMMENT '유저 ID',
    `date_issued`  datetime(6) NOT NULL COMMENT '발급 일시',
    `date_used`    datetime(6) NULL COMMENT '사용 일시',
    `date_created` datetime(6) NOT NULL COMMENT '생성 일시',
    `date_updated` datetime(6) NOT NULL COMMENT '수정 일시',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
   DEFAULT CHARSET = utf8mb4
     COMMENT '쿠폰 발급 내역';

insert into coupons (title, coupon_type, total_quantity, issued_quantity, discount_amount, min_available_amount, date_issue_start, date_issue_end, date_created, date_updated)
values ("네고왕 선착순 테스트 쿠폰", "FIRST_COME_FIRST_SERVED", 10, 0, 100000, 110000, "2024-09-01", "2024-12-31", "2024-09-01", "2024-09-01");

update coupons set total_quantity = 500 where id = 1;

-- 초기화
delete from coupon_issues;

update coupons set issued_quantity = 0 where id = 1;

select * from coupon_issues ci ;

select * from coupons c ;
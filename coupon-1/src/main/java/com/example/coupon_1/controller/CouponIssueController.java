package com.example.coupon_1.controller;

import com.example.coupon_1.controller.dto.CouponIssueRequestDto;
import com.example.coupon_1.controller.dto.CouponIssueResponseDto;
import com.example.coupon_1.service.CouponIssueRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CouponIssueController {

    private final CouponIssueRequestService couponIssueRequestService;

    @PostMapping("/v1/issue")
    public CouponIssueResponseDto issueV1(@RequestBody CouponIssueRequestDto body) {
        couponIssueRequestService.issueRequestV1(body);
        return new CouponIssueResponseDto(true, null);
    }

    @PostMapping("/v2/issue")
    public CouponIssueResponseDto issueV2(@RequestBody CouponIssueRequestDto body) {
        couponIssueRequestService.issueRequestV2(body);
        return new CouponIssueResponseDto(true, null);
    }

    @PostMapping("/v3/issue")
    public CouponIssueResponseDto issueV3(@RequestBody CouponIssueRequestDto body) {
        couponIssueRequestService.issueRequestV3(body);
        return new CouponIssueResponseDto(true, null);
    }

    @PostMapping("/v4/issue")
    public CouponIssueResponseDto issueV4(@RequestBody CouponIssueRequestDto body) {
        couponIssueRequestService.issueRequestV4(body);
        return new CouponIssueResponseDto(true, null);
    }

    @PostMapping("/v5/issue")
    public CouponIssueResponseDto issueV5(@RequestBody CouponIssueRequestDto body) {
        couponIssueRequestService.issueRequestV5(body);
        return new CouponIssueResponseDto(true, null);
    }

    @PostMapping("/v1/issue-async")
    public CouponIssueResponseDto asyncIssueV1(@RequestBody CouponIssueRequestDto body) {
        couponIssueRequestService.asyncIssueRequestV1(body);
        return new CouponIssueResponseDto(true, null);
    }
}

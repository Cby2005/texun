package com.agriculture.points.controller;

import com.agriculture.common.security.UserContext;
import com.agriculture.common.result.Result;
import com.agriculture.points.entity.AgriUserPointsAccount;
import com.agriculture.points.entity.AgriUserPointsRecord;
import com.agriculture.points.service.PointsAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
@Tag(name = "积分账户")
public class PointsController {

    private final PointsAccountService pointsAccountService;

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    public Result<AgriUserPointsAccount> myAccount() {
        return Result.ok(pointsAccountService.getAccount(UserContext.getCurrentUserId()));
    }

    @GetMapping("/my/records")
    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    public Result<List<AgriUserPointsRecord>> myRecords() {
        return Result.ok(pointsAccountService.listRecords(UserContext.getCurrentUserId()));
    }
}

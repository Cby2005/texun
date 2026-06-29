package com.agriculture.points.controller;

import com.agriculture.common.result.Result;
import com.agriculture.points.entity.AgriPointsExchangeOrder;
import com.agriculture.points.entity.AgriPointsGoods;
import com.agriculture.points.service.PointsMallService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points/mall")
@RequiredArgsConstructor
@Tag(name = "积分商城")
public class PointsMallController {

    private final PointsMallService mallService;

    // ==================== 商品浏览（农户） ====================
    @GetMapping("/goods")
    public Result<List<AgriPointsGoods>> listGoods(@RequestParam(required = false) String category) {
        return Result.ok(mallService.listGoods(category));
    }

    @GetMapping("/goods/{id}")
    public Result<AgriPointsGoods> getGoods(@PathVariable Long id) {
        return Result.ok(mallService.getGoods(id));
    }

    // ==================== 兑换（农户） ====================
    @PostMapping("/exchange/{goodsId}")
    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    public Result<AgriPointsExchangeOrder> exchange(@PathVariable Long goodsId, @RequestBody Map<String, Object> body) {
        Integer qty = body.get("quantity") != null ? Integer.valueOf(body.get("quantity").toString()) : 1;
        return Result.ok(mallService.exchange(goodsId, qty,
                (String) body.getOrDefault("receiverName", ""),
                (String) body.getOrDefault("receiverPhone", ""),
                (String) body.getOrDefault("receiverAddress", "")));
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    public Result<List<AgriPointsExchangeOrder>> myOrders() {
        return Result.ok(mallService.listMyOrders());
    }

    @PostMapping("/orders/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        mallService.cancelOrder(id); return Result.ok();
    }

    // ==================== 商品管理（管理员） ====================
    @PostMapping("/goods")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> createGoods(@RequestBody AgriPointsGoods goods) {
        mallService.createGoods(goods); return Result.ok();
    }

    @PutMapping("/goods/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> updateGoods(@PathVariable Long id, @RequestBody AgriPointsGoods goods) {
        mallService.updateGoods(id, goods); return Result.ok();
    }

    @PutMapping("/goods/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> updateGoodsStatus(@PathVariable Long id, @RequestParam String status) {
        mallService.updateGoodsStatus(id, status); return Result.ok();
    }

    @GetMapping("/all-orders")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<List<AgriPointsExchangeOrder>> allOrders() {
        return Result.ok(mallService.listAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<Void> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        mallService.updateOrderStatus(id, status); return Result.ok();
    }

    /** 管理员也可见全部商品 */
    @GetMapping("/all-goods")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<List<AgriPointsGoods>> allGoods() {
        return Result.ok(mallService.listGoods(null));
    }
}

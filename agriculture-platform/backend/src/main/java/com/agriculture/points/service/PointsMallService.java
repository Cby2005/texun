package com.agriculture.points.service;

import com.agriculture.common.notification.entity.AgriNotification;
import com.agriculture.common.notification.mapper.AgriNotificationMapper;
import com.agriculture.common.security.UserContext;
import com.agriculture.points.entity.AgriPointsExchangeOrder;
import com.agriculture.points.entity.AgriPointsGoods;
import com.agriculture.points.mapper.AgriPointsExchangeOrderMapper;
import com.agriculture.points.mapper.AgriPointsGoodsMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointsMallService {
    private final AgriPointsGoodsMapper goodsMapper;
    private final AgriPointsExchangeOrderMapper orderMapper;
    private final PointsAccountService pointsAccountService;
    private final AgriNotificationMapper notificationMapper;

    // ==================== 商品管理 ====================
    public List<AgriPointsGoods> listGoods(String category) {
        LambdaQueryWrapper<AgriPointsGoods> qw = new LambdaQueryWrapper<>();
        qw.eq(AgriPointsGoods::getStatus, "ON_SALE");
        if (category != null && !category.isEmpty()) qw.eq(AgriPointsGoods::getGoodsCategory, category);
        qw.orderByDesc(AgriPointsGoods::getCreateTime);
        return goodsMapper.selectList(qw);
    }

    public AgriPointsGoods getGoods(Long id) { return goodsMapper.selectById(id); }

    public void createGoods(AgriPointsGoods goods) {
        goods.setStatus("OFF_SALE");
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        goodsMapper.insert(goods);
    }

    public void updateGoods(Long id, AgriPointsGoods goods) {
        goods.setId(id); goods.setUpdateTime(LocalDateTime.now()); goodsMapper.updateById(goods);
    }

    public void updateGoodsStatus(Long id, String status) {
        AgriPointsGoods goods = goodsMapper.selectById(id);
        if (goods != null) { goods.setStatus(status); goods.setUpdateTime(LocalDateTime.now()); goodsMapper.updateById(goods); }
    }

    // ==================== 兑换 ====================
    @Transactional
    public AgriPointsExchangeOrder exchange(Long goodsId, Integer quantity, String name, String phone, String address) {
        AgriPointsGoods goods = goodsMapper.selectById(goodsId);
        if (goods == null || !"ON_SALE".equals(goods.getStatus())) throw new RuntimeException("商品不可兑换");
        if (quantity == null || quantity < 1) quantity = 1;
        if (goods.getStock() < quantity) throw new RuntimeException("库存不足");
        Long userId = UserContext.getCurrentUserId();
        int totalPoints = goods.getRequiredPoints() * quantity;

        // 扣积分
        pointsAccountService.spendPoints(userId, totalPoints, "MALL_EXCHANGE", goodsId, "兑换商品: " + goods.getGoodsName());

        // 扣库存
        goods.setStock(goods.getStock() - quantity);
        if (goods.getStock() <= 0) goods.setStatus("SOLD_OUT");
        goods.setUpdateTime(LocalDateTime.now());
        goodsMapper.updateById(goods);

        // 生成订单
        String orderNo = "EX" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + userId;
        AgriPointsExchangeOrder order = new AgriPointsExchangeOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setGoodsId(goodsId);
        order.setGoodsName(goods.getGoodsName());
        order.setRequiredPoints(goods.getRequiredPoints());
        order.setExchangeQuantity(quantity);
        order.setTotalPoints(totalPoints);
        order.setOrderStatus("PENDING");
        order.setReceiverName(name);
        order.setReceiverPhone(phone);
        order.setReceiverAddress(address);
        order.setExchangeTime(LocalDateTime.now());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 通知
        sendNotification(userId, "MALL_ORDER", "MALL_ORDER", order.getId(),
                "兑换成功", "您已成功兑换" + goods.getGoodsName() + "，消耗" + totalPoints + "积分。");

        return order;
    }

    public List<AgriPointsExchangeOrder> listMyOrders() {
        return orderMapper.selectList(new LambdaQueryWrapper<AgriPointsExchangeOrder>()
                .eq(AgriPointsExchangeOrder::getUserId, UserContext.getCurrentUserId())
                .orderByDesc(AgriPointsExchangeOrder::getCreateTime));
    }

    public List<AgriPointsExchangeOrder> listAllOrders() {
        return orderMapper.selectList(new LambdaQueryWrapper<AgriPointsExchangeOrder>()
                .orderByDesc(AgriPointsExchangeOrder::getCreateTime));
    }

    public void updateOrderStatus(Long id, String status) {
        AgriPointsExchangeOrder order = orderMapper.selectById(id);
        if (order != null) {
            order.setOrderStatus(status);
            if ("COMPLETED".equals(status)) order.setFinishTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);
        }
    }

    @Transactional
    public void cancelOrder(Long id) {
        AgriPointsExchangeOrder order = orderMapper.selectById(id);
        if (order == null) throw new RuntimeException("订单不存在");
        if (!"PENDING".equals(order.getOrderStatus())) throw new RuntimeException("只能取消待处理订单");
        // 退积分
        pointsAccountService.earnPoints(order.getUserId(), order.getTotalPoints(), "REFUND", id, "兑换取消退款");
        // 退库存
        AgriPointsGoods goods = goodsMapper.selectById(order.getGoodsId());
        if (goods != null) {
            goods.setStock(goods.getStock() + order.getExchangeQuantity());
            if (goods.getStock() > 0 && "SOLD_OUT".equals(goods.getStatus())) goods.setStatus("ON_SALE");
            goodsMapper.updateById(goods);
        }
        order.setOrderStatus("CANCELLED");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    private void sendNotification(Long userId, String noticeType, String bizType, Long bizId, String title, String content) {
        AgriNotification n = new AgriNotification();
        n.setUserId(userId); n.setTitle(title); n.setContent(content);
        n.setNoticeType(noticeType); n.setBizType(bizType); n.setBizId(bizId);
        n.setReadStatus(0); n.setCreateTime(LocalDateTime.now()); n.setUpdateTime(LocalDateTime.now());
        notificationMapper.insert(n);
    }
}

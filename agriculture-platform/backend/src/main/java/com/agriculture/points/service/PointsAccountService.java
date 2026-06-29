package com.agriculture.points.service;

import com.agriculture.points.mapper.AgriUserPointsAccountMapper;
import com.agriculture.points.mapper.AgriUserPointsRecordMapper;
import com.agriculture.points.entity.AgriUserPointsAccount;
import com.agriculture.points.entity.AgriUserPointsRecord;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointsAccountService {
    private final AgriUserPointsAccountMapper accountMapper;
    private final AgriUserPointsRecordMapper recordMapper;

    private AgriUserPointsAccount getOrCreateAccount(Long userId) {
        AgriUserPointsAccount account = accountMapper.selectOne(
                new LambdaQueryWrapper<AgriUserPointsAccount>().eq(AgriUserPointsAccount::getUserId, userId));
        if (account == null) {
            account = new AgriUserPointsAccount();
            account.setUserId(userId);
            account.setTotalPoints(0);
            account.setAvailablePoints(0);
            account.setFrozenPoints(0);
            account.setUsedPoints(0);
            account.setCreateTime(LocalDateTime.now());
            account.setUpdateTime(LocalDateTime.now());
            accountMapper.insert(account);
        }
        return account;
    }

    @Transactional
    public void earnPoints(Long userId, int points, String sourceType, Long sourceId, String remark) {
        AgriUserPointsAccount account = getOrCreateAccount(userId);
        int before = account.getAvailablePoints();
        account.setTotalPoints(account.getTotalPoints() + points);
        account.setAvailablePoints(account.getAvailablePoints() + points);
        account.setUpdateTime(LocalDateTime.now());
        accountMapper.updateById(account);

        AgriUserPointsRecord record = new AgriUserPointsRecord();
        record.setUserId(userId);
        record.setPointsChange(points);
        record.setChangeType("EARN");
        record.setSourceType(sourceType);
        record.setSourceId(sourceId);
        record.setBeforePoints(before);
        record.setAfterPoints(account.getAvailablePoints());
        record.setRemark(remark);
        record.setCreateTime(LocalDateTime.now());
        recordMapper.insert(record);
    }

    @Transactional
    public void spendPoints(Long userId, int points, String sourceType, Long sourceId, String remark) {
        AgriUserPointsAccount account = getOrCreateAccount(userId);
        if (account.getAvailablePoints() < points) throw new RuntimeException("积分不足");
        int before = account.getAvailablePoints();
        account.setAvailablePoints(account.getAvailablePoints() - points);
        account.setUsedPoints(account.getUsedPoints() + points);
        account.setUpdateTime(LocalDateTime.now());
        accountMapper.updateById(account);

        AgriUserPointsRecord record = new AgriUserPointsRecord();
        record.setUserId(userId);
        record.setPointsChange(-points);
        record.setChangeType("SPEND");
        record.setSourceType(sourceType);
        record.setSourceId(sourceId);
        record.setBeforePoints(before);
        record.setAfterPoints(account.getAvailablePoints());
        record.setRemark(remark);
        record.setCreateTime(LocalDateTime.now());
        recordMapper.insert(record);
    }

    public AgriUserPointsAccount getAccount(Long userId) {
        return getOrCreateAccount(userId);
    }

    public java.util.List<AgriUserPointsRecord> listRecords(Long userId) {
        return recordMapper.selectList(new LambdaQueryWrapper<AgriUserPointsRecord>()
                .eq(AgriUserPointsRecord::getUserId, userId).orderByDesc(AgriUserPointsRecord::getCreateTime));
    }
}

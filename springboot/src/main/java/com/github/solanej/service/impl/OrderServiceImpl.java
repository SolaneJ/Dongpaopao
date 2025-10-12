package com.github.solanej.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.solanej.common.R;
import com.github.solanej.mapper.AddressMapper;
import com.github.solanej.service.AddressService;
import com.github.solanej.service.OrderService;

import com.github.solanej.mapper.OrderMapper;
import com.github.solanej.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    private final AddressMapper addressMapper;

    @Override
    public R createOrder(JSONObject params) {

        JSONObject deliverInfo = params.getJSONObject("deliverInfo");
        JSONObject feeInfo = params.getJSONObject("feeInfo");

        Order order = new Order();
        // 下单人
        order.setXdr(params.getString("uid"));
        // 期望送达时间
//        order.setExpectTime(deliverInfo.get("expectTime"));
        // 订单类型
        order.setOrderType(params.getString("type").charAt(0));
        // 地址
        order.setAid(deliverInfo.getString("aid"));
        // 订单数据
        order.setDetail(params.getString("businessInfo"));
        // 金额
        order.setAmount(feeInfo.getBigDecimal("totalFee"));

        orderMapper.insert(order);
        return R.success();
    }

    @Override
    public R acceptOrder(JSONObject params) {
        String oid = params.getString("oid");
        String uid = params.getString("uid");

        /* 采用数据库状态更新方案，防止并发问题 */
        int effectRows = orderMapper.update(new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus, 'J')         // 接单状态改为进行中
                .set(Order::getJdr, uid)
                .set(Order::getAcceptTime, LocalDateTime.now())
                .eq(Order::getOid, oid)
                .eq(Order::getStatus, 'D'));
        if (effectRows == 0) {
            return R.error("订单不存在或已被接单");
        }
        return R.success();
    }

    @Override
    public R listOrder(String uid) {
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
//                        .ne(Order::getXdr, uid)
                        .eq(Order::getStatus, 'D')
                        .orderByDesc(Order::getCreateTime));
        return R.success(orders);
    }

    @Override
    public R detailOrder(String oid) {
        JSONObject result = new JSONObject();

        Order order = orderMapper.selectById(oid);
        result.put("order", order);
        // 地址
        result.put("address", addressMapper.selectById(order.getAid()));
        return R.success(result);
    }
}

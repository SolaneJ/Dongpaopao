package com.github.solanej.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;
import com.github.solanej.service.OrderService;

import com.github.solanej.mapper.OrderMapper;
import com.github.solanej.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Override
    public R createOrder(JSONObject params) {

        JSONObject deliverInfo = params.getJSONObject("deliverInfo");
        JSONObject feeInfo = params.getJSONObject("feeInfo");

        Order order = new Order();
        // 下单人
        order.setXdr(params.getString("uid"));
        // 订单类型
        order.setOrder_type(params.getString("type").charAt(0));
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
    public R listOrder() {
        return R.success();
    }
}

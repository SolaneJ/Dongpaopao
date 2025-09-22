package com.github.solanej.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;
import com.github.solanej.service.OrderService;

import com.github.solanej.mapper.OrderMapper;
import com.github.solanej.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Override
    public R createOrder(JSONObject params) {
        orderMapper.insert(new Order());
        return R.success();
    }

    @Override
    public R listOrder() {
        return R.success();
    }
}

package com.github.solanej.controller;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;
import com.github.solanej.service.OrderService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public R createOrder(@RequestBody JSONObject params) {
        return orderService.createOrder(params);
    }

    @GetMapping("/list")
    public R listOrder(@RequestParam("uid") @Nullable String uid) {
        return orderService.listOrder(uid);
    }

    @GetMapping("/detail")
    public R detailOrder(@RequestParam("oid") String oid) {
        return orderService.detailOrder(oid);
    }
}

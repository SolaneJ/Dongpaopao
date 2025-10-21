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

    /**
     * 接单
     *
     * @return 接单结果
     */
    @PutMapping("/accept")
    public R acceptOrder(@RequestBody JSONObject params) {
        return orderService.acceptOrder(params);
    }

    /**
     * @param uid 用户id
     * @return 订单列表
     */
    @GetMapping("/list")
    public R listOrder(@RequestParam("uid") @Nullable String uid) {
        return orderService.listOrder(uid);
    }

    /**
     * @param oid 订单id
     * @return 订单详情
     */
    @GetMapping("/detail")
    public R detailOrder(@RequestParam("oid") String oid) {
        return orderService.detailOrder(oid);
    }

    /**
     * 获取进行中的订单列表
     *
     * @param uid 用户id
     * @return 进行中的订单列表
     */
    @GetMapping("/progressing")
    public R progressingOrder(@Nullable @RequestParam("type") String type, @RequestParam("uid") String uid) {
        return orderService.progressingOrder(type, uid);
    }
}

package com.github.solanej.controller;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;
import com.github.solanej.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 创建交易记录
     *
     * @param param 交易记录参数
     * @return 交易记录ID
     */
    @PostMapping("/create")
    public R create(@RequestBody JSONObject param) {
        return transactionService.create(param);
    }

    /**
     * 获取用户交易记录
     *
     * @param uid 用户ID
     * @return 交易记录列表
     */
    @GetMapping("/list")
    public R list(@RequestParam("uid") String uid) {
        return transactionService.list(uid);
    }
}

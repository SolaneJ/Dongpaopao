package com.github.solanej.service;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;

public interface TransactionService {

    R create(JSONObject param);

    R list(String uid);
}

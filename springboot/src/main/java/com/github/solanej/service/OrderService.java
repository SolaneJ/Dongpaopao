package com.github.solanej.service;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;

public interface OrderService {

    R createOrder(JSONObject params);

    R listOrder(String uid);
}

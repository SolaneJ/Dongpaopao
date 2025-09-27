package com.github.solanej.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.solanej.common.R;
import com.github.solanej.entity.Address;
import com.github.solanej.mapper.AddressMapper;
import com.github.solanej.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public R addAddress(JSONObject params) {
        Address address = params.to(Address.class);
        addressMapper.insert(address);
        return R.success();
    }

    @Override
    public R deleteAddress(JSONObject params) {
        return null;
    }

    @Override
    public R updateAddress(JSONObject params) {
        return null;
    }

    @Override
    public R list(String uid) {
        List<Address> list = addressMapper.selectList(new LambdaQueryWrapper<Address>().eq(Address::getUid, uid));
        return R.success(list);
    }
}

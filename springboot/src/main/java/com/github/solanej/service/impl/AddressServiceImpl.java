package com.github.solanej.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.solanej.common.R;
import com.github.solanej.entity.Address;
import com.github.solanej.mapper.AddressMapper;
import com.github.solanej.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public R addAddress(JSONObject params) {
        long count = addressMapper.selectCount(new LambdaQueryWrapper<Address>().eq(Address::getUid, params.getString("uid")));
        if (count >= 5) {
            return R.error("最多添加5个");
        }

        Address address = params.to(Address.class);

        /* 如果设定为默认地址，就要取消其他默认地址 */
        if (address.getIsDefault()) {
            addressMapper.update(null, new LambdaUpdateWrapper<Address>().eq(Address::getUid, address.getUid()).set(Address::getIsDefault, false));
        }

        addressMapper.insert(address);
        return R.success("添加成功");
    }

    @Override
    public R deleteAddress(JSONObject params) {
        addressMapper.deleteById(params.getLong("aid"));
        return R.success("删除成功");
    }

    @Override
    @Transactional
    public R updateAddress(JSONObject params) {
        Address address = params.to(Address.class);
        /* 如果设定为默认地址，就要取消其他默认地址 */
        if (address.getIsDefault()) {
            addressMapper.update(null, new LambdaUpdateWrapper<Address>().eq(Address::getUid, address.getUid()).set(Address::getIsDefault, false));
        }
        addressMapper.update(address, new LambdaUpdateWrapper<Address>().eq(Address::getAid, address.getAid()));
        return R.success("更新成功");
    }

    @Override
    public R list(String uid) {
        List<Address> list = addressMapper.selectList(new LambdaQueryWrapper<Address>().eq(Address::getUid, uid));
        return R.success(list);
    }
}

package com.github.solanej.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.solanej.entity.Address;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}

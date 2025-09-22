package com.github.solanej.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.solanej.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}

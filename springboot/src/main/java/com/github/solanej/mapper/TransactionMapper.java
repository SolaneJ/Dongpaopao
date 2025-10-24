package com.github.solanej.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.solanej.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {
}

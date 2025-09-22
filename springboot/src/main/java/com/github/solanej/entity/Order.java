package com.github.solanej.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("`order`")
public class Order {

    @TableId(type = IdType.ASSIGN_ID)
    private String oid;

    private String xdr;

    private String create_time;

    private Character order_type;

    private Integer aid;

    private JSONObject detail;

    private String remark;

    private BigDecimal amount;

    private String jdr;

    private String complete_time;

    private Character status;
}

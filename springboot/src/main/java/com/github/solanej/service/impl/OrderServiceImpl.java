package com.github.solanej.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.solanej.common.R;
import com.github.solanej.entity.Conversation;
import com.github.solanej.entity.Order;
import com.github.solanej.entity.User;
import com.github.solanej.mapper.AddressMapper;
import com.github.solanej.mapper.ConversationMapper;
import com.github.solanej.mapper.OrderMapper;
import com.github.solanej.mapper.UserMapper;
import com.github.solanej.service.OrderService;
import com.github.solanej.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    private final AddressMapper addressMapper;

    private final ConversationMapper conversationMapper;

    private final TransactionService transactionService;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public R createOrder(JSONObject params) {
        JSONObject deliverInfo = params.getJSONObject("deliverInfo");
        JSONObject feeInfo = params.getJSONObject("feeInfo");

        BigDecimal totalFee = feeInfo.getBigDecimal("totalFee");
        String uid = params.getString("uid");

        // 参数校验
        if (totalFee == null || totalFee.compareTo(BigDecimal.ZERO) <= 0) {
            return R.error("金额非法");
        }

        // 1. 先扣减余额（原子操作）
        boolean deductSuccess = userMapper.update(null, new LambdaUpdateWrapper<User>()
                .setSql("balance = balance - " + totalFee)
                .eq(User::getUid, uid)
                .ge(User::getBalance, totalFee)) > 0;

        if (!deductSuccess) {
            return R.error("余额不足");
        }

        // 2. 创建订单
        Order order = new Order();
        order.setXdr(uid);
        order.setOrderType(params.getString("type").charAt(0));
        order.setExpectTime(params.getLocalDateTime("expectTime"));
        order.setAid(deliverInfo.getString("aid"));
        order.setDetail(params.getString("businessInfo"));
        order.setAmount(totalFee);
        order.setStatus('D');
        order.setCreateTime(LocalDateTime.now());

        int insertResult = orderMapper.insert(order);
        if (insertResult <= 0) {
            // 如果订单创建失败，需要回滚余额（这里需要事务支持）
            userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .setSql("balance = balance + " + totalFee)
                    .eq(User::getUid, uid));
            return R.error("创建订单失败");
        }

        // 3. 记录交易流水
        JSONObject transactionParam = new JSONObject();
        transactionParam.put("oid", order.getOid());
        transactionParam.put("uid", uid);
        transactionParam.put("type", "ORDER");
        transactionParam.put("amount", totalFee);
        transactionService.create(transactionParam);

        return R.success();
    }

    @Override
    public R acceptOrder(JSONObject params) {
        String oid = params.getString("oid");
        String uid = params.getString("uid");

        /* 采用数据库状态更新方案，防止并发问题 */
        int effectRows = orderMapper.update(new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus, 'J')         // 接单状态改为进行中
                .set(Order::getJdr, uid)
                .set(Order::getAcceptTime, LocalDateTime.now())
                .eq(Order::getOid, oid)
                .eq(Order::getStatus, 'D'));
        if (effectRows == 0) {
            return R.error("订单不存在或已被接单");
        }
        return R.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateOrderStatus(JSONObject params) {
        String oid = params.getString("oid");
        String status = params.getString("status");

        // 先查询订单信息，获取金额和用户ID
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOid, oid));

        if (order == null) {
            return R.error("订单不存在");
        }

        // 更新订单状态
        int updateResult = orderMapper.update(new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus, status.charAt(0))
                .set(Order::getCompleteTime, LocalDateTime.now())
                .eq(Order::getOid, oid));

        if (updateResult <= 0) {
            return R.error("更新订单状态失败");
        }

        // 如果状态更新为已完成（S），则处理金额返还和流水记录
        if ('S' == status.charAt(0)) {
            // 1. 将金额返还给用户（增加余额）
            boolean refundSuccess = userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .setSql("balance = balance + " + order.getAmount())
                    .eq(User::getUid, order.getJdr())) > 0;

            if (!refundSuccess) {
                throw new RuntimeException("返还金额失败");
            }

            // 2. 在流水表中添加完成订单记录（收入为正数）
            JSONObject transactionParam = new JSONObject();
            transactionParam.put("oid", oid);
            transactionParam.put("uid", order.getXdr());
            transactionParam.put("type", "COMPLETE_ORDER");
            transactionParam.put("amount", order.getAmount()); // 收入为正数
            transactionService.create(transactionParam);
        }

        if ('C' == status.charAt(0)) {
            // 3. 如果状态更新为已取消（C），则处理金额返还和流水记录
            // 1. 将金额返还给用户（增加余额）
            boolean refundSuccess = userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .setSql("balance = balance + " + order.getAmount())
                    .eq(User::getUid, order.getXdr())) > 0;

            if (!refundSuccess) {
                throw new RuntimeException("返还金额失败");
            }

            // 2. 在流水表中添加取消订单记录（收入为正数）
            JSONObject transactionParam = new JSONObject();
            transactionParam.put("oid", oid);
            transactionParam.put("uid", order.getXdr());
            transactionParam.put("type", "CANCEL_ORDER");
            transactionParam.put("amount", order.getAmount()); // 收入为正数
            transactionService.create(transactionParam);
        }

        return R.success();
    }

    @Override
    public R listOrder(String uid) {
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .ne(Order::getXdr, uid)
                        .eq(Order::getStatus, 'D')
                        .orderByDesc(Order::getCreateTime));
        return R.success(orders);
    }

    @Override
    public R listMyOrders(String uid, String role, String status, String type, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getXdr, uid).or().eq(Order::getJdr, uid);
        if (role != null && !role.isEmpty()) {
            if ("xdr".equals(role)) {
                wrapper.eq(Order::getXdr, uid);
            } else if ("jdr".equals(role)) {
                wrapper.eq(Order::getJdr, uid);
            }
        } else {
            wrapper.eq(Order::getXdr, uid).or().eq(Order::getJdr, uid);
        }

        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status.charAt(0));
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Order::getOrderType, type.charAt(0));
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return R.success(orderMapper.selectList(wrapper));
    }

    @Override
    public R detailOrder(String oid) {
        JSONObject result = new JSONObject();

        Order order = orderMapper.selectById(oid);
        result.put("order", order);
        // 地址
        result.put("address", addressMapper.selectById(order.getAid()));
        // 如果已接单，就查询会话信息
        if (order.getStatus() != 'D' && order.getStatus() != 'C') {
            Conversation conversation = conversationMapper.selectOne(new LambdaQueryWrapper<Conversation>().eq(Conversation::getOid, oid));
            result.put("conversationId", conversation.getCid());
        }
        return R.success(result);
    }

    @Override
    public R progressingOrder(String type, String uid) {

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, 'J')
                .orderByDesc(Order::getAcceptTime);

        if (type == null) {
            wrapper.eq(Order::getXdr, uid).or().eq(Order::getJdr, uid);
        } else if ("xdr".equals(type)) {
            wrapper.eq(Order::getXdr, uid);
        } else if ("jdr".equals(type)) {
            wrapper.eq(Order::getJdr, uid);
        }

        return R.success(orderMapper.selectList(wrapper));
    }
}

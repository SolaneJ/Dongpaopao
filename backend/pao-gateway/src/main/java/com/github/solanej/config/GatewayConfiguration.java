package com.github.solanej.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
public class GatewayConfiguration {

    @Bean
    @Order(-1)  // 确保 Sentinel 过滤器先于其他过滤器加载
    public GlobalFilter globalFilter() {
        return new SentinelGatewayFilter();
    }

    /**
     * 硬编码方式加载限流规则，可通过数据源的方式来动态加载限流规则<a href="https://sentinelguard.io/zh-cn/docs/dynamic-rule-configuration.html">...</a>
     */
    @PostConstruct
    public void initSentinelRules() {
        final HashSet<GatewayFlowRule> flowRules = new HashSet<>();

        flowRules.add(new GatewayFlowRule("pao-order-service")
                .setCount(2)
                .setIntervalSec(1));
        // 加载限流规则
        GatewayRuleManager.loadRules(flowRules);
    }
}

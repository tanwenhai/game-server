package com.twh.gamegate.configuration

import com.twh.commons.loadbalancer.BaseLoadBalancer
import com.twh.commons.loadbalancer.ILoadBalancer
import com.twh.commons.loadbalancer.INode
import com.twh.commons.loadbalancer.RoundRobinRule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoadBalancerConfiguration {
    @Bean
    fun loadBalancer(): ILoadBalancer<INode> = BaseLoadBalancer<INode>(RoundRobinRule<INode>())
}
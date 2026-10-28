package com.damai.service.strategy;

import com.damai.dto.ProgramOrderCreateDto;

/**
 * @description: 节目订单策略
 **/
public interface ProgramOrderStrategy {
    
    /**
     * 创建订单
     * @param programOrderCreateDto 订单参数
     * @return 订单编号
     * */
    String createOrder(ProgramOrderCreateDto programOrderCreateDto);
    
    /**
     * 获取版本号
     * @return 版本号
     * */
    String version();
}

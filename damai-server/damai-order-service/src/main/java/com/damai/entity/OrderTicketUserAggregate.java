package com.damai.entity;

import lombok.Data;

/**
 * @description: 购票人订单聚合统计 实体
 **/
@Data
public class OrderTicketUserAggregate {
    
    private Long orderNumber;
    
    private Integer orderTicketUserCount;
}

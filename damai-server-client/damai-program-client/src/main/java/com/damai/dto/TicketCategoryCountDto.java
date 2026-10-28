package com.damai.dto;

import lombok.Data;

/**
 * @description: 票据数据操作 dto
 **/
@Data
public class TicketCategoryCountDto {
    
    /**
     * 票档id
     * */
    private Long ticketCategoryId;
    
    /**
     * 数量
     * */
    private Long count;
}

package com.damai.pay;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description: 支付结果
 **/
@Data
@AllArgsConstructor
public class PayResult {
    
    private final boolean success;
    
    private final String body;
}

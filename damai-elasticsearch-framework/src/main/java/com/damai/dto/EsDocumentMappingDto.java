package com.damai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: elasticsearch文档映射
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsDocumentMappingDto {
    
    /**
     * 字段名
     * */
    private String paramName;
    
    /**
     * 字段类型
     * */
    private String paramType;
}

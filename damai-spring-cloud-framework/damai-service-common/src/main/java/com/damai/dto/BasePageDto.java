package com.damai.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @description: 分页dto
 **/
@Data
public class BasePageDto {
    
    
    @NotNull
    private Integer pageNumber;
    
    
    @NotNull
    private Integer pageSize;
}

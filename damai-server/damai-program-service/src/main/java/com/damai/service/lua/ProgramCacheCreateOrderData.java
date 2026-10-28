package com.damai.service.lua;

import com.damai.vo.SeatVo;
import lombok.Data;

import java.util.List;

/**
 * @description: 节目缓存更新 实体
 **/
@Data
public class ProgramCacheCreateOrderData {

    private Integer code;
    
    private List<SeatVo> purchaseSeatList;
}

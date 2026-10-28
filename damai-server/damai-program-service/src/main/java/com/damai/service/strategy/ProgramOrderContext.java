package com.damai.service.strategy;

import com.damai.enums.BaseCode;
import com.damai.exception.DaMaiFrameException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @description: 节目订单上下文
 **/
@Component
public class ProgramOrderContext {
    
    /**
     * ⚠️注意！在升级的大麦pro版本中进行了优化，将不同的订单版本进一步细化拆分，并进行了详细的压测
     **/
    private static final Map<String,ProgramOrderStrategy> MAP = new HashMap<>(8);
    
    @Autowired
    private List<ProgramOrderStrategy> programOrderStrategyList;
    
    @PostConstruct
    public void init() {
        for (ProgramOrderStrategy programOrderStrategy : programOrderStrategyList) {
            MAP.put(programOrderStrategy.version(), programOrderStrategy);
        }
    }
    
    public ProgramOrderStrategy get(String version){
        return Optional.ofNullable(MAP.get(version)).orElseThrow(() -> 
                new DaMaiFrameException(BaseCode.PROGRAM_ORDER_STRATEGY_NOT_EXIST));
    }
}

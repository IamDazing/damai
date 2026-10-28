package com.damai.service.strategy.impl;

import com.damai.core.RepeatExecuteLimitConstants;
import com.damai.dto.ProgramOrderCreateDto;
import com.damai.enums.CompositeCheckType;
import com.damai.enums.ProgramOrderVersion;
import com.damai.initialize.impl.composite.CompositeContainer;
import com.damai.repeatexecutelimit.annotion.RepeatExecuteLimit;
import com.damai.service.ProgramOrderService;
import com.damai.service.strategy.BaseProgramOrder;
import com.damai.service.strategy.ProgramOrderStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.damai.core.DistributedLockConstants.PROGRAM_ORDER_CREATE_V4;

/**
 * @description: 节目订单v4
 **/
@Slf4j
@Component
public class ProgramOrderV4Strategy implements ProgramOrderStrategy {
    
    @Autowired
    private ProgramOrderService programOrderService;
    
    @Autowired
    private BaseProgramOrder baseProgramOrder;
    
    @Autowired
    private CompositeContainer compositeContainer;
    
    /**
     * ⚠️注意！在升级的大麦pro版本中进行了优化，解决了数据一致性的问题
     **/
    @RepeatExecuteLimit(
            name = RepeatExecuteLimitConstants.CREATE_PROGRAM_ORDER,
            keys = {"#programOrderCreateDto.userId","#programOrderCreateDto.programId"})
    @Override
    public String createOrder(ProgramOrderCreateDto programOrderCreateDto) {
        compositeContainer.execute(CompositeCheckType.PROGRAM_ORDER_CREATE_CHECK.getValue(),programOrderCreateDto);
        return baseProgramOrder.localLockCreateOrder(PROGRAM_ORDER_CREATE_V4,programOrderCreateDto,
                () -> programOrderService.createNewAsync(programOrderCreateDto));
    }
    
    @Override
    public String version() {
        return ProgramOrderVersion.V4_VERSION.getVersion();
    }
}

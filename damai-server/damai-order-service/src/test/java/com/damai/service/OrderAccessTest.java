package com.damai.service;

import com.damai.constant.Constant;
import com.damai.dto.*;
import com.damai.entity.Order;
import com.damai.exception.DaMaiFrameException;
import com.damai.mapper.OrderMapper;
import com.damai.threadlocal.BaseParameterHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderAccessTest {
    @AfterEach void clear() { BaseParameterHolder.removeParameterMap(); }

    @Test void otherUsersCannotReadCancelOrPayAnOrder() {
        OrderService service = spy(new OrderService());
        OrderMapper mapper = mock(OrderMapper.class);
        ReflectionTestUtils.setField(service, "orderMapper", mapper);
        Order order = new Order(); order.setOrderNumber(123L); order.setUserId(42L); order.setOrderStatus(1);
        when(mapper.selectOne(any())).thenReturn(order);
        OrderGetDto get = new OrderGetDto(); get.setOrderNumber(123L);
        OrderCancelDto cancel = new OrderCancelDto(); cancel.setOrderNumber(123L);
        OrderPayDto pay = new OrderPayDto(); pay.setOrderNumber(123L); pay.setChannel("alipay");
        OrderPayCheckDto check = new OrderPayCheckDto(); check.setOrderNumber(123L);

        assertThrows(DaMaiFrameException.class, () -> service.get(get));
        BaseParameterHolder.setParameter(Constant.USER_ID, "43");
        assertThrows(DaMaiFrameException.class, () -> service.get(get));
        assertThrows(DaMaiFrameException.class, () -> service.initiateCancel(cancel));
        assertThrows(DaMaiFrameException.class, () -> service.pay(pay));
        assertThrows(DaMaiFrameException.class, () -> service.payCheck(check));
        verify(mapper, never()).update(any(), any());

        BaseParameterHolder.setParameter(Constant.USER_ID, "42");
        doReturn(true).when(service).cancel(cancel);
        assertTrue(service.initiateCancel(cancel));
    }
}

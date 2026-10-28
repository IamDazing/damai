package com.damai.controller;

import com.damai.constant.Constant;
import com.damai.dto.OrderListDto;
import com.damai.exception.DaMaiFrameException;
import com.damai.service.OrderService;
import com.damai.threadlocal.BaseParameterHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderIdentityTest {
    @AfterEach void clear() { BaseParameterHolder.removeParameterMap(); }
    @Test void requestedUserCannotOverrideAuthenticatedUser() {
        OrderService service = mock(OrderService.class);
        OrderController controller = new OrderController();
        ReflectionTestUtils.setField(controller, "orderService", service);
        OrderListDto dto = new OrderListDto();
        dto.setUserId(999L);
        BaseParameterHolder.setParameter(Constant.USER_ID, "123");
        controller.selectList(dto);
        assertEquals(123L, dto.getUserId());
        verify(service).selectList(dto);
    }
    @Test void missingIdentityCannotQueryOrders() {
        OrderController controller = new OrderController();
        OrderListDto dto = new OrderListDto(); dto.setUserId(123L);
        assertThrows(DaMaiFrameException.class, () -> controller.selectList(dto));
    }
    @Test void sessionCheckDoesNotQueryOrders() {
        OrderService service = mock(OrderService.class);
        OrderController controller = new OrderController();
        ReflectionTestUtils.setField(controller, "orderService", service);
        OrderListDto dto = new OrderListDto(); dto.setUserId(0L); dto.setSessionOnly(true);
        BaseParameterHolder.setParameter(Constant.USER_ID, "123");
        assertTrue(controller.selectList(dto).getData().isEmpty());
        verifyNoInteractions(service);
    }
}

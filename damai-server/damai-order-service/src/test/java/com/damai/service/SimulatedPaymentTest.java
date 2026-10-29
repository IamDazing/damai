package com.damai.service;

import com.damai.constant.Constant;
import com.damai.dto.OrderPayDto;
import com.damai.entity.Order;
import com.damai.enums.OrderStatus;
import com.damai.exception.DaMaiFrameException;
import com.damai.mapper.OrderMapper;
import com.damai.mapper.OrderTicketUserMapper;
import com.damai.service.properties.OrderProperties;
import com.damai.threadlocal.BaseParameterHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SimulatedPaymentTest {
    @AfterEach void clear() { BaseParameterHolder.removeParameterMap(); }

    @Test void onlyTheOwnerCanCompleteAnEnabledDemoPayment() {
        OrderService service = new OrderService();
        OrderMapper orders = mock(OrderMapper.class);
        OrderTicketUserMapper tickets = mock(OrderTicketUserMapper.class);
        OrderService proxy = mock(OrderService.class);
        OrderProperties properties = new OrderProperties(); properties.setDemoPaymentEnabled(true);
        ReflectionTestUtils.setField(service, "orderMapper", orders);
        ReflectionTestUtils.setField(service, "orderTicketUserMapper", tickets);
        ReflectionTestUtils.setField(service, "orderService", proxy);
        ReflectionTestUtils.setField(service, "orderProperties", properties);
        Order order = new Order(); order.setOrderNumber(123456L); order.setUserId(42L);
        order.setOrderStatus(1); order.setOrderPrice(new BigDecimal("100.00"));
        when(orders.selectOne(any())).thenReturn(order);
        when(orders.update(any(), any())).thenReturn(1);
        when(tickets.update(any(), any())).thenReturn(1);
        OrderPayDto dto = new OrderPayDto(); dto.setOrderNumber(123456L); dto.setPrice(new BigDecimal("100.00"));

        BaseParameterHolder.setParameter(Constant.USER_ID, "43");
        assertThrows(DaMaiFrameException.class, () -> service.simulatePay(dto));
        verifyNoInteractions(proxy);

        BaseParameterHolder.setParameter(Constant.USER_ID, "42");
        dto.setPrice(BigDecimal.ONE);
        assertThrows(DaMaiFrameException.class, () -> service.simulatePay(dto));
        verifyNoInteractions(proxy);
        dto.setPrice(new BigDecimal("100.00"));
        assertEquals("DEMO_PAID", service.simulatePay(dto));
        verify(proxy).updateOrderRelatedData(123456L, OrderStatus.PAY);
        verify(orders).update(argThat(value -> Integer.valueOf(99).equals(value.getPayOrderType())), any());
        verify(tickets).update(argThat(value -> Integer.valueOf(99).equals(value.getPayOrderType())), any());

        order.setOrderStatus(3); order.setPayOrderType(99);
        assertEquals("DEMO_PAID", service.simulatePay(dto));
        verify(proxy, times(1)).updateOrderRelatedData(123456L, OrderStatus.PAY);

        properties.setDemoPaymentEnabled(false);
        assertThrows(DaMaiFrameException.class, () -> service.simulatePay(dto));
    }
}

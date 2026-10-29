package com.damai.controller;

import com.damai.constant.Constant;
import com.damai.dto.TicketUserDto;
import com.damai.dto.TicketUserIdDto;
import com.damai.dto.TicketUserListDto;
import com.damai.entity.TicketUser;
import com.damai.exception.DaMaiFrameException;
import com.damai.mapper.TicketUserMapper;
import com.damai.service.TicketUserService;
import com.damai.threadlocal.BaseParameterHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketUserAccessTest {
    @AfterEach void clear() { BaseParameterHolder.removeParameterMap(); }

    @Test void publicListAndAddRequireMatchingIdentityWhileInternalListWorks() {
        TicketUserController controller = new TicketUserController();
        TicketUserService service = mock(TicketUserService.class);
        ReflectionTestUtils.setField(controller, "ticketUserService", service);
        TicketUserListDto list = new TicketUserListDto(); list.setUserId(42L);
        TicketUserDto add = new TicketUserDto(); add.setUserId(42L);
        assertThrows(DaMaiFrameException.class, () -> controller.list(list));
        controller.listInternal(list);
        verify(service).list(list);
        BaseParameterHolder.setParameter(Constant.USER_ID, "43");
        assertThrows(DaMaiFrameException.class, () -> controller.list(list));
        assertThrows(DaMaiFrameException.class, () -> controller.add(add));
        BaseParameterHolder.setParameter(Constant.USER_ID, "42");
        controller.list(list);
        controller.add(add);
        verify(service, times(2)).list(list);
        verify(service).add(add);
    }

    @Test void deletingTicketUserRequiresItsOwner() {
        TicketUserService service = spy(new TicketUserService());
        TicketUserMapper mapper = mock(TicketUserMapper.class);
        ReflectionTestUtils.setField(service, "ticketUserMapper", mapper);
        TicketUser ticket = new TicketUser(); ticket.setId(123L); ticket.setUserId(42L);
        when(mapper.selectById(123L)).thenReturn(ticket);
        TicketUserIdDto dto = new TicketUserIdDto(); dto.setId(123L);
        assertThrows(DaMaiFrameException.class, () -> service.delete(dto));
        BaseParameterHolder.setParameter(Constant.USER_ID, "43");
        assertThrows(DaMaiFrameException.class, () -> service.delete(dto));
        verify(mapper, never()).deleteById(123L);
        BaseParameterHolder.setParameter(Constant.USER_ID, "42");
        doNothing().when(service).delTicketUserVoListCache("42");
        service.delete(dto);
        verify(mapper).deleteById(123L);
    }
}

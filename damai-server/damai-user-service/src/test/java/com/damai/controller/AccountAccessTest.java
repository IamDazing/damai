package com.damai.controller;

import com.damai.constant.Constant;
import com.damai.dto.*;
import com.damai.exception.DaMaiFrameException;
import com.damai.service.UserService;
import com.damai.threadlocal.BaseParameterHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountAccessTest {
    @AfterEach void clear() { BaseParameterHolder.removeParameterMap(); }

    @Test void accountEndpointsRejectMissingOrOtherUserIdentity() {
        UserController controller = new UserController();
        UserService service = mock(UserService.class);
        ReflectionTestUtils.setField(controller, "userService", service);
        UserIdDto read = new UserIdDto(); read.setId(42L);
        assertThrows(DaMaiFrameException.class, () -> controller.getById(read));
        BaseParameterHolder.setParameter(Constant.USER_ID, "43");
        assertThrows(DaMaiFrameException.class, () -> controller.getById(read));
        UserUpdateDto update = new UserUpdateDto(); update.setId(42L);
        UserUpdatePasswordDto password = new UserUpdatePasswordDto(); password.setId(42L);
        UserUpdateEmailDto email = new UserUpdateEmailDto(); email.setId(42L);
        UserUpdateMobileDto mobile = new UserUpdateMobileDto(); mobile.setId(42L);
        UserAuthenticationDto identity = new UserAuthenticationDto(); identity.setId(42L);
        assertThrows(DaMaiFrameException.class, () -> controller.update(update));
        assertThrows(DaMaiFrameException.class, () -> controller.updatePassword(password));
        assertThrows(DaMaiFrameException.class, () -> controller.updateEmail(email));
        assertThrows(DaMaiFrameException.class, () -> controller.updateMobile(mobile));
        assertThrows(DaMaiFrameException.class, () -> controller.authentication(identity));
        verifyNoInteractions(service);

        BaseParameterHolder.setParameter(Constant.USER_ID, "42");
        controller.getById(read);
        controller.update(update);
        controller.updatePassword(password);
        controller.updateEmail(email);
        controller.updateMobile(mobile);
        controller.authentication(identity);
        verify(service).getById(read);
        verify(service).update(update);
        verify(service).updatePassword(password);
        verify(service).updateEmail(email);
        verify(service).updateMobile(mobile);
        verify(service).authentication(identity);
    }
}

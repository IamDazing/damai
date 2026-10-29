package com.damai.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.damai.dto.UserUpdatePasswordDto;
import com.damai.entity.User;
import com.damai.exception.DaMaiFrameException;
import com.damai.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UpdatePasswordTest {
    @Test void updateRequiresCurrentPassword() {
        UserService service = new UserService();
        UserMapper mapper = mock(UserMapper.class);
        ReflectionTestUtils.setField(service, "userMapper", mapper);
        UserUpdatePasswordDto dto = new UserUpdatePasswordDto();
        dto.setId(42L); dto.setOldPassword("old-password"); dto.setPassword("new-password");
        when(mapper.selectOne(any(Wrapper.class))).thenReturn(null, new User());
        assertThrows(DaMaiFrameException.class, () -> service.updatePassword(dto));
        verify(mapper, never()).updateById(any(User.class));
        service.updatePassword(dto);
        verify(mapper).updateById(argThat((User user) -> user.getId().equals(42L) && user.getPassword().equals("new-password")));
    }
}

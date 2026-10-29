package com.damai.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.damai.core.SpringUtil;
import com.damai.dto.UserLoginDto;
import com.damai.entity.UserEmail;
import com.damai.entity.UserMobile;
import com.damai.exception.DaMaiFrameException;
import com.damai.mapper.UserEmailMapper;
import com.damai.mapper.UserMapper;
import com.damai.mapper.UserMobileMapper;
import com.damai.redis.RedisCache;
import com.damai.redis.RedisKeyBuild;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LoginFailureLimitTest {
    private UserService service;
    private RedisCache cache;
    private UserMapper users;
    private UserMobileMapper mobiles;
    private UserEmailMapper emails;
    private AtomicInteger failures;

    @BeforeEach void setUp() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        ConfigurableEnvironment env = mock(ConfigurableEnvironment.class);
        when(context.getEnvironment()).thenReturn(env);
        when(env.getProperty(anyString(), anyString())).thenReturn("test");
        new SpringUtil().initialize(context);
        service = new UserService();
        cache = mock(RedisCache.class); users = mock(UserMapper.class);
        mobiles = mock(UserMobileMapper.class); emails = mock(UserEmailMapper.class);
        ReflectionTestUtils.setField(service, "redisCache", cache);
        ReflectionTestUtils.setField(service, "userMapper", users);
        ReflectionTestUtils.setField(service, "userMobileMapper", mobiles);
        ReflectionTestUtils.setField(service, "userEmailMapper", emails);
        failures = new AtomicInteger();
        when(cache.get(any(RedisKeyBuild.class), eq(String.class))).thenAnswer(i -> String.valueOf(failures.get()));
        when(cache.incrBy(any(RedisKeyBuild.class), eq(1L))).thenAnswer(i -> (long) failures.incrementAndGet());
    }

    @Test void incorrectMobilePasswordIsLimitedAfterFiveFailures() {
        UserMobile mobile = new UserMobile(); mobile.setUserId(42L);
        when(mobiles.selectOne(any(Wrapper.class))).thenReturn(mobile);
        UserLoginDto dto = new UserLoginDto(); dto.setMobile("13800000000"); dto.setPassword("incorrect");
        for (int i = 0; i < 6; i++) assertThrows(DaMaiFrameException.class, () -> service.login(dto));
        verify(cache, times(5)).incrBy(any(RedisKeyBuild.class), eq(1L));
        verify(cache, times(5)).expire(any(RedisKeyBuild.class), eq(1L), eq(TimeUnit.MINUTES));
        verify(users, times(5)).selectOne(any(Wrapper.class));
    }

    @Test void incorrectEmailPasswordAlsoIncrementsFailureCounter() {
        UserEmail email = new UserEmail(); email.setUserId(42L);
        when(emails.selectOne(any(Wrapper.class))).thenReturn(email);
        UserLoginDto dto = new UserLoginDto(); dto.setEmail("example@example.invalid"); dto.setPassword("incorrect");
        assertThrows(DaMaiFrameException.class, () -> service.login(dto));
        verify(cache).incrBy(any(RedisKeyBuild.class), eq(1L));
        verify(cache).expire(any(RedisKeyBuild.class), eq(1L), eq(TimeUnit.MINUTES));
    }
}

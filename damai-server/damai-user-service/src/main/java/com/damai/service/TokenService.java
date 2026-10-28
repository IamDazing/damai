package com.damai.service;

import com.alibaba.fastjson.JSONObject;
import com.damai.core.RedisKeyManage;
import com.damai.util.StringUtil;
import com.damai.enums.BaseCode;
import com.damai.exception.DaMaiFrameException;
import com.damai.jwt.TokenUtil;
import com.damai.redis.RedisCache;
import com.damai.redis.RedisKeyBuild;
import com.damai.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

/**
 * @description: token service
 **/

@Component
public class TokenService {
    
    @Value("${LEGACY_TOKEN_SECRET:}")
    private String tokenSecret;
    
    @Autowired
    private RedisCache redisCache;
    
    public String parseToken(String token){
        String userStr = TokenUtil.parseToken(token,tokenSecret);
        if (StringUtil.isNotEmpty(userStr)) {
            return JSONObject.parseObject(userStr).getString("userId");
        }
        return null;
    }
    
    public UserVo getUser(String token){
        UserVo userVo = null;
        String userId = parseToken(token);
        if (StringUtil.isNotEmpty(userId)) {
            userVo = redisCache.get(RedisKeyBuild.createRedisKey(RedisKeyManage.USER_LOGIN, userId), UserVo.class);
        }
        return Optional.ofNullable(userVo).orElseThrow(() -> new DaMaiFrameException(BaseCode.USER_EMPTY));
    }
}

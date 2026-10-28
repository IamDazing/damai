package com.damai;

import org.springframework.data.redis.connection.stream.ObjectRecord;

/**
 * @description: redis-stream消息处理
 **/
@FunctionalInterface
public interface MessageConsumer {
    
    /**
     * 消息处理
     * @param message 消息
     * 
     * */
    void accept(ObjectRecord<String, String> message);
}
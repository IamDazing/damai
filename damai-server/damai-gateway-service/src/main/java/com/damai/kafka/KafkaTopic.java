package com.damai.kafka;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**
 * @description: kafka topic
 **/
@Data
public class KafkaTopic {
    
    @Value("${spring.kafka.topic:default}")
    private String topic;

}

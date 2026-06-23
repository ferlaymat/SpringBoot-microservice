package com.example.product.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.stock-reserved}")
    private String stockReservedTopic;

    @Value("${kafka.topics.stock-failed}")
    private String stockFailedTopic;

    @Value("${kafka.topics.stock-compensated}")
    private String stockCompensatedTopic;

    @Bean
    public NewTopic stockReservedTopic() {
        return TopicBuilder.name(stockReservedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic stockFailedTopic() {
        return TopicBuilder.name(stockFailedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic stockCompensatedTopic() {
        return TopicBuilder.name(stockCompensatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
package com.bioinformatics_pipeline.variant_calling_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic variantsCalledTopic() {
        return TopicBuilder.name("variants-called")
              .build();
    }
}

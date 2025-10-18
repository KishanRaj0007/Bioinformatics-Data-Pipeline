package com.bioinformatics_pipeline.alignment_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic alignmentCompleteTopic() {
        return TopicBuilder.name("alignment-complete")
               .build();
    }
}

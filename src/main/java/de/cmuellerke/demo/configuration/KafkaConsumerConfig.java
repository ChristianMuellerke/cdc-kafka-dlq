package de.cmuellerke.demo.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import de.cmuellerke.demo.entity.User;

@Configuration
public class KafkaConsumerConfig {
    
    @Bean
    public ConsumerFactory<String, User> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,KafkaProducerConfig.BOOTSTRAP_SERVER);
        //configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test123");

        JsonDeserializer<User> userJsonDeserializer = new JsonDeserializer<>();
        userJsonDeserializer.addTrustedPackages("de.cmuellerke.demo.*");

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), userJsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, User> 
      kafkaListenerContainerFactory() {
   
        ConcurrentKafkaListenerContainerFactory<String, User> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }    
}

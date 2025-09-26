package com.adminservice.adminservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    public static final String CHECK_ROLE_1 = "check_role_queue_1";
    public static final String CHECK_ROLE_2 = "check_role_queue_2";
    public static final String ADD_STUDENT_QUEUE = "add_student_queue";
    public static final String ADD_STAFF_QUEUE = "add_staff_queue";


    @Bean
    public Queue checkRoleQueue1() {
        return new Queue(CHECK_ROLE_1);
    }

    @Bean
    public Queue checkRoleQueue2() {
        return new Queue(CHECK_ROLE_2);
    }

    @Bean
    public Queue addStudentQueue() {
        return new Queue(ADD_STUDENT_QUEUE);
    }

    @Bean
    public Queue addStaffQueue() {
        return new Queue(ADD_STAFF_QUEUE);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages(
                "com.shared.dtos",
                "com.applicationservice.dtos"
        );

        converter.setClassMapper(classMapper);
        return converter;
    }


    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(converter);
        configurer.configure(factory, connectionFactory);
        return factory;
    }
}

package com.studentservice.student.configuration.rabbitconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.FanoutExchange;
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

    public static final String GENERATE_CERTIFICATE_QUEUE = "generate_certificate_queue";
    public static final String CHECK_ROLE_EXCHANGE = "check_role_exchange";
    public static final String ADD_STUDENT_QUEUE = "add_student_queue";
    public static final String ADD_ASSIGNMENT_QUEUE = "add_assignment_queue";
    public static final String ADD_CAT_SUBMISSION_QUEUE = "add_cat_submission_queue";
    public static final String ADD_QUIZ_SUBMISSION_QUEUE = "add_quiz_submission_queue";
    public static final String GET_NOTIFICATIONS = "get_notifications";



    @Bean
    public Queue addStudentQueue() {
        return new Queue(ADD_STUDENT_QUEUE);
    }

    @Bean
    public Queue generateCertificateQueue() {
        return new Queue(GENERATE_CERTIFICATE_QUEUE);
    }


    @Bean
    public Queue addAssignment() {
        return new Queue(ADD_ASSIGNMENT_QUEUE);
    }

    @Bean
    public Queue addCatSubmissionQueue() {
        return new Queue(ADD_CAT_SUBMISSION_QUEUE);
    }

    @Bean
    public Queue addQuizSubmissionQueue() {
        return new Queue((ADD_QUIZ_SUBMISSION_QUEUE));
    }
    @Bean
    public Queue getNotificationsQueue(){return new Queue(GET_NOTIFICATIONS);}

    @Bean
    public FanoutExchange checkRoleExchange() {
        return new FanoutExchange(CHECK_ROLE_EXCHANGE);
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

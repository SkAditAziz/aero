package dev.example.aero.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class MessageSenderConfig {
    @Value("${activemq.broker-url}")
    private String brokerUrl;

    public ActiveMQConnectionFactory senderActiveMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        return activeMQConnectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(senderActiveMQConnectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(cachingConnectionFactory());
    }

    // TODO add preDestroy to resolve it?
    /*
    o.a.c.loader.WebappClassLoaderBase       : The web application [ROOT] appears to have started a thread named [ActiveMQ Transport: tcp://localhost/127.0.0.1:61616@44014] but has failed to stop it. This is very likely to create a memory leak. Stack trace of thread:
     */
}

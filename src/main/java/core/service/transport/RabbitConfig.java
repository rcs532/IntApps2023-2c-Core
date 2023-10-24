package core.service.transport;


import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import com.rabbitmq.client.ConnectionFactory;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitConfig {

  @Value("${spring.rabbitmq.host}")
  String host;

  @Value("${spring.rabbitmq.username}")
  String username;

  @Value("${spring.rabbitmq.password}")
  String password;

  @Value("${spring.rabbitmq.port}")
  int port;

  public static final String ADMIN_PERSONAL = "admin-personal-queue";

  public static final String ANALITICA = "analitica-queue";

  public static final String CORE_BANCARIO = "core-bancario-queue";

  public static final String CORE_CONTABLE = "core-contable-queue";

  public static final String MARKETPLACE = "marketplace-queue";

  public static final String ROBOTS = "robots-queue";

  public static final String USUARIOS = "usuarios-queue";

  public static final String CORE_EXCHANGE = "core-exchange";


  @Bean
  public ConnectionFactory connectionFactory() throws KeyManagementException, NoSuchAlgorithmException{
    ConnectionFactory factory = new ConnectionFactory();

    factory.setHost(host);
    factory.setUsername(username);
    factory.setPassword(password);
    factory.setPort(port);
    factory.useSslProtocol();

    return factory;
  }

  @Bean
  @Scope(value = "prototype")
  public RabbitConsumer rabbitConsumer(String queue, String destination){
    return new RabbitConsumer(queue, destination);
  }

  @Bean
  public org.springframework.amqp.rabbit.connection.ConnectionFactory springConnectionFactory(ConnectionFactory rabConnectionFactory){
    return new CachingConnectionFactory(rabConnectionFactory);
  }

  @Bean
  public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory){
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    return rabbitTemplate;
  }

  @Bean
  public Producer producer(RabbitTemplate rabbitTemplate){
    return new Producer(rabbitTemplate);
  }

  @Bean
  public Queue robotsQueue() {return new Queue(ROBOTS);}

  @Bean
  public Queue marketplaceQueue() {return new Queue(MARKETPLACE);}

  @Bean
  public Queue personalAdminQueue() {return new Queue(ADMIN_PERSONAL);}

  @Bean
  public Queue coreBankingQueue() {return new Queue(CORE_BANCARIO);}

  @Bean
  public Queue coreAccountingQueue() {return new Queue(CORE_CONTABLE);}

  @Bean
  public Queue usersQueue(){
    return new Queue(USUARIOS);
  }

  @Bean
  public Queue analyticsQueue(){
    return new Queue(ANALITICA);
  }

  @Bean
  public Binding analyticsBinding(Queue analyticsQueue, DirectExchange directExchange){
    return BindingBuilder.bind(analyticsQueue).to(directExchange).with(ANALITICA);
  }

  @Bean
  public Binding robotsBinding(Queue robotsQueue, DirectExchange directExchange){
    return BindingBuilder.bind(robotsQueue).to(directExchange).with(ROBOTS);
  }

  @Bean
  public Binding usersBinding(Queue usersQueue, DirectExchange directExchange){
    return BindingBuilder.bind(usersQueue).to(directExchange).with(USUARIOS);
  }

  @Bean
  public Binding accountingCoreBinding(Queue coreAccountingQueue, DirectExchange directExchange){
    return BindingBuilder.bind(coreAccountingQueue).to(directExchange).with(CORE_CONTABLE);
  }

  @Bean
  public Binding bankingCoreBinding(Queue coreBankingQueue, DirectExchange directExchange){
    return BindingBuilder.bind(coreBankingQueue).to(directExchange).with(CORE_BANCARIO);
  }
  @Bean
  public Binding personalAdminBinding(Queue personalAdminQueue, DirectExchange directExchange){
    return BindingBuilder.bind(personalAdminQueue).to(directExchange).with(ADMIN_PERSONAL);
  }

  @Bean
  public Binding maketplaceBinding(Queue marketplaceQueue, DirectExchange directExchange){
    return BindingBuilder.bind(marketplaceQueue).to(directExchange).with(MARKETPLACE);
  }

  @Bean
  public DirectExchange directExchange(){
    return new DirectExchange(CORE_EXCHANGE);
  }
}

package core.service.transport;


import com.rabbitmq.client.Connection;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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



//connectionFactory is a bean that is used to create a connection to the RabbitMQ server
  @Bean
  public ConnectionFactory connectionFactory() throws KeyManagementException, NoSuchAlgorithmException, Exception{
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);
    factory.setUsername(username);
    factory.setPassword(password);
    factory.setPort(port);
    factory.useSslProtocol();


    return factory;
  }

  //rabbitAdmin is a bean that is used to declare queues, exchanges, bindings, etc.
  @Bean
  @Scope(value = "prototype")
  public RabbitConsumer rabbitConsumer(String queue, String destination){
    return new RabbitConsumer(queue, destination);
  }

  //springConnectionFactory is a bean that is used to cache the connections to the RabbitMQ server
  @Bean
  public org.springframework.amqp.rabbit.connection.ConnectionFactory springConnectionFactory(ConnectionFactory rabConnectionFactory){
    return new CachingConnectionFactory(rabConnectionFactory);
  }

  //rabbitTemplate is a bean that is used to send messages to the queues
  @Bean
  public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory){
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    return rabbitTemplate;
  } // Sirve para enviar mensajes a las colas

  //
  @Bean
  public Producer producer(RabbitTemplate rabbitTemplate){
    return new Producer(rabbitTemplate);
  }

  // Queues
  //robotsQueue is a bean that is used to declare a queue named robots-queue
  @Bean
  public Queue robotsQueue() {return new Queue(ROBOTS);}

  //marketplaceQueue is a bean that is used to declare a queue named marketplace-queue
  @Bean
  public Queue marketplaceQueue() {return new Queue(MARKETPLACE);}

  //personalAdminQueue is a bean that is used to declare a queue named admin-personal-queue
  @Bean
  public Queue personalAdminQueue() {return new Queue(ADMIN_PERSONAL);}

  //coreBankingQueue is a bean that is used to declare a queue named core-bancario-queue
  @Bean
  public Queue coreBankingQueue() {return new Queue(CORE_BANCARIO);}

  //coreAccountingQueue is a bean that is used to declare a queue named core-contable-queue
  @Bean
  public Queue coreAccountingQueue() {return new Queue(CORE_CONTABLE);}

  //usersQueue is a bean that is used to declare a queue named usuarios-queue
  @Bean
  public Queue usersQueue(){
    return new Queue(USUARIOS);
  }

  //analyticsQueue is a bean that is used to declare a queue named analitica-queue
  @Bean
  public Queue analyticsQueue(){
    return new Queue(ANALITICA);
  }

  // Exchanges
  //analyticsExchange is a bean that is used to declare an exchange named core-exchange
  @Bean
  public Binding analyticsBinding(Queue analyticsQueue, DirectExchange directExchange){
    return BindingBuilder.bind(analyticsQueue).to(directExchange).with(ANALITICA);
  } // Sirve para enlazar la cola con el exchange


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

  //directExchange is a bean that is used to declare an exchange named core-exchange
  @Bean
  public DirectExchange directExchange(){
    return new DirectExchange(CORE_EXCHANGE);
  }
  //Se crea un exchange llamado core-exchange
}

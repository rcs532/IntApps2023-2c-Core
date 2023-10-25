package core.service.transport.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import core.service.transport.RabbitConsumer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

  private static final Logger logger = LogManager.getLogger(WebSocketConfig.class);

  @Autowired
  private BeanFactory beanFactory; // BeanFactory is used to create new instances of RabbitConsumer

  HashMap<String, ArrayList<String>> clientSubscriptions; // Key: SessionId, Value: List of Subscriptions
  HashMap<String, ConsumerPair> consumers; // Key: Destination, Value: ConsumerPair

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) { // Message Broker Configuration
    config.enableSimpleBroker(WebSocketConstants.PREFIX_TOPIC); // Topic Prefix
    config.setApplicationDestinationPrefixes(WebSocketConstants.PREFIX_APP); // App Prefix
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // Register Endpoints
    for (var itr : WebSocketConstants.ENDPOINTS.keySet()){ // Iterate over endpoints in WebSocketConstants
      registry.addEndpoint("/" + itr).withSockJS();
      registry.addEndpoint("/" + itr);
    }

    clientSubscriptions = new HashMap<>(); // Initialize HashMaps
    consumers = new HashMap<>(); // Initialize HashMaps
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    // Configure Client Inbound Channel
    registration.interceptors(new ChannelInterceptor() {
      // Add Interceptor to see handle when a client subscribes or unsubscribes
      @Override
      public
      void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        // Post Send Method sent to /app/send/queue
        logger.info("POST SEND: " + message.toString());

        Object stompHeader = message.getHeaders().get("stompCommand"); // Get Stomp Command
        String sessionId = (String) message.getHeaders().get("simpSessionId");

        if (sessionId == null || stompHeader == null)
          return;

        StompCommand command = (StompCommand) stompHeader;
        switch(command){ // Switch on Stomp Command
          case SUBSCRIBE:{ // If Subscribe
            String destination = (String) message.getHeaders().get("simpDestination");
            logger.info("DESTINATION: " + destination);
            registerSubscribe(destination);
            // Add to clientSubscriptions
            clientSubscriptions.get(sessionId).add(destination);
            break;
          }
          case UNSUBSCRIBE:{
            // Disconnect consumer from rabbit




            break;
          }
          case DISCONNECT:{ // If Disconnect
            // Remove from clientSubscriptions
            ArrayList<String> subscriptions = clientSubscriptions.get(sessionId);

            if (subscriptions == null)
              return;

            for(String sub : subscriptions){
              logger.info("Disconnecting Points: " + sub + " from Session: " + sessionId);
              unRegisterSubscribe(sub);
            }

            clientSubscriptions.remove(sessionId);

            break;
          }
          case CONNECT:{ // If Connect
            // Add to clientSubscriptions
            if (!clientSubscriptions.containsKey(sessionId)){
              clientSubscriptions.put(sessionId, new ArrayList<String>());
            }
            break;
          }
          default:{
            break;
          }
        }
      }
    });
  }


  @Scheduled(fixedDelay = 600 * 1000)
  public void checkConnections(){
    // Check Connections every 10 minutes and log the number of connections
    for (var itr : consumers.keySet()){
      ConsumerPair info = consumers.get(itr);
      logger.info("Listen counter for Channel: " + itr + "--" + info.getCounter());
    }
  }

  //registerSubscribe is a method that registers a new consumer for a destination
  public void registerSubscribe(String destination){
    if (!consumers.containsKey(destination)){
      String queueName = WebSocketConstants.QueueNameMaps.get(destination);

      if (queueName == null)
        return;

      RabbitConsumer consumer = beanFactory.getBean(RabbitConsumer.class, queueName, destination);
      logger.info("QUEUE: " + queueName);
      consumers.put(destination, new ConsumerPair(consumer));
    }
    else{
      ConsumerPair pair = consumers.get(destination);
      pair.increaseCounter();
    }
  }

  //unRegisterSubscribe is a method that unregisters a consumer for a destination
  public void unRegisterSubscribe(String destination){
    if (!consumers.containsKey(destination)){
      return;
    }
    else{
      ConsumerPair pair = consumers.get(destination);
      pair.decreaseCounter();

      if (pair.getCounter() <= 0){
        pair = null;
        consumers.remove(destination);
      }
    }
  }
//ConsumerPair is a class that contains a RabbitConsumer and a counter
  private class ConsumerPair{

    private RabbitConsumer consumer;
    public RabbitConsumer getConsumer() {
      return consumer;
    }

    private AtomicInteger counter;

    public Integer getCounter(){
      return counter.get();
    }

    public void increaseCounter() {
      counter.incrementAndGet();
    }

    public void decreaseCounter(){
      counter.decrementAndGet();
    }

    public ConsumerPair(RabbitConsumer cRabbitConsumer){
      consumer = cRabbitConsumer;
      counter = new AtomicInteger(1);
    }

  }
}

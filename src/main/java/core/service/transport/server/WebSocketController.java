package core.service.transport.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import core.service.transport.Producer;
import core.service.transport.RabbitConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
public class WebSocketController {

  @Autowired
  Producer producer;
  
  private static final Logger logger = LogManager.getLogger(WebSocketController.class);

  @MessageMapping("/send/usuarios")
  public void sendToUserQueue(String message){
    logger.info("Mensaje Enviado a la Cola de Usuarios: " + message);
    producer.sendTo(RabbitConfig.USUARIOS, message);
  }

  @MessageMapping("/send/core-bancario")
  public void sendToCoreBanking(String message){
    logger.info("Mensaje Enviado a la Cola de Core Banking: " + message);
    producer.sendTo(RabbitConfig.CORE_BANCARIO, message);
  }

  @MessageMapping("/send/core-contable")
  public void sendToCoreAccounting(String message){
    logger.info("Mensaje Enviado a la Cola de Core Contable: " + message);
    producer.sendTo(RabbitConfig.CORE_CONTABLE, message);
  }

  @MessageMapping("/send/robots")
  public void sendToRobots(String message){
    logger.info("Mensaje Enviado a la Cola de Robots: " + message);
    producer.sendTo(RabbitConfig.ROBOTS, message);
  }
  @MessageMapping("/send/analitica")
  public void sendToAnalytics(String message){
    logger.info("Mensaje Enviado a la cola de  Analitica: " + message);
    producer.sendTo(RabbitConfig.ANALITICA, message);
  }
  @MessageMapping("/send/admin-personal")
  public void sendToPersonalAdmin(String message){
    logger.info("Mensaje Enviado a la cola de administracion personal: " + message);
    producer.sendTo(RabbitConfig.ADMIN_PERSONAL, message);
  }

  @MessageMapping("/send/marketplace")
  public void sendToMarketplace(String message){
    logger.info("Mensaje Enviado a la cola de Marketplace: " + message);
    producer.sendTo(RabbitConfig.MARKETPLACE, message);
  }




}

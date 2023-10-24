package core.service.transport.server;

import java.util.Map;

import core.service.transport.RabbitConfig;

public class WebSocketConstants {
  
  public static final Map<String, String> ENDPOINTS = Map.of(
          "analitica", "ws://localhost:8080/analitica",
          "robots", "ws://localhost:8080/robots",
          "core-bancario", "ws://localhost:8080/core-bancario",
          "core-contable", "ws://localhost:8080/core-contable",
          "marketing", "ws://localhost:8080/marketplace",
          "usuarios", "ws://localhost:8080/usuarios",
          "admin-personal","ws://localhost:8080/admin-personal"
  );

  public static final Map<String, String> QueueNameMaps = Map.of(
          "/topic/robots", RabbitConfig.ROBOTS,
          "/topic/marketplace", RabbitConfig.MARKETPLACE,
          "/topic/admin-personal", RabbitConfig.ADMIN_PERSONAL,
          "/topic/core-bancario", RabbitConfig.CORE_BANCARIO,
          "/topic/core-contable", RabbitConfig.CORE_CONTABLE,
          "/topic/usuarios", RabbitConfig.USUARIOS,
          "/topic/analitica", RabbitConfig.ANALITICA
  );

  public static final String PREFIX_APP = "/app";
  public static final String PREFIX_TOPIC = "/topic";
}

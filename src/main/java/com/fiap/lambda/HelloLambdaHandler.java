package com.fiap.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

public class HelloLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        LambdaLogger logger = context.getLogger();

        // Log some information for debugging
        logger.log("Handling request from Lambda with event: " + event);

        // Prepare the response
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);  // HTTP status 200 (OK)
        response.setBody("{\"message\":\"Hello from Lambda!\"}");  // JSON response

        // Optionally set headers if needed
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);

        return response;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("client", r -> r
                        .path("/client")
                        .uri("http://localhost:8082/"))
                .route("staff", r -> r
                        .path("/staff")
                        .uri("http://localhost:8083/"))
                .route("cep - Brasilapi", r -> r
                        .path("/cep/v1/**")
                        .filters(f -> f.rewritePath("/cep/v1/(?<cep>.*)", "/api/cep/v1/${cep}"))
                        .uri("https://brasilapi.com.br"))
                .route("cep - viacep", r -> r
                        .path("/cep/v2/**")
                        .filters(f -> f.rewritePath("/cep/v2/(?<cep>.*)", "/ws/${cep}/json/"))
                        .uri("https://viacep.com.br"))
                .build();

    }


}

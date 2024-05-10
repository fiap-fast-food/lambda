package com.fiap.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

public class AuthenticateCustomerLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AuthenticateCustomerLambda.class);
    private final RestTemplate restTemplate;
    private final String customerApiBaseUrl = "https://example.com/api";

    public AuthenticateCustomerLambda() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String cpf = input.getPathParameters().get("cpf");
        if (cpf == null || cpf.isEmpty()) {
            return createErrorResponse(400, "CPF é obrigatório.");
        }

        boolean isAuthenticated = checkCustomerExistence(cpf);
        if (isAuthenticated) {
            return createSuccessResponse("Cliente autenticado com sucesso.");
        } else {
            return createErrorResponse(404, "Cliente não encontrado.");
        }
    }

    private boolean checkCustomerExistence(String cpf) {
        try {
            String url = customerApiBaseUrl + "/identify/" + cpf;
            ResponseEntity<?> result = restTemplate.getForEntity(url, Object.class);
            return result.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    private APIGatewayProxyResponseEvent createSuccessResponse(String message) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(message);
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(message);
    }
}
package com.fiap.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthenticateCustomerLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiUrl = "http://localhost:8080/";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String cpf = input.getPathParameters().get("cpf");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            URL url = new URL(apiUrl + "api/v1/customers/identify/" + cpf);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int statusCode = connection.getResponseCode();
            String responseBody = objectMapper.writeValueAsString(connection.getResponseMessage());

            response.setStatusCode(statusCode);
            response.setBody(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatusCode(500);
            response.setBody("Erro interno do servidor.");
        }

        return response;
    }
}

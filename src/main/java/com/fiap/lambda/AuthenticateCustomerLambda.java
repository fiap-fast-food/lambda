package com.fiap.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthenticateCustomerLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DataSourceProperties db;
    private final CognitoAutenticator cognitoAutenticator;

    public AuthenticateCustomerLambda() {
        this.db = new DataSourceProperties();
        this.cognitoAutenticator = new CognitoAutenticator();
    }
    public AuthenticateCustomerLambda(DataSourceProperties db, CognitoAutenticator cognitoAutenticator) {
        this.db = db;
        this.cognitoAutenticator = cognitoAutenticator;

    }

    @SneakyThrows
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        String cpf = input.getPathParameters().get("cpf");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try(Connection conn = DriverManager.getConnection(db.getHost(), db.getUsername(), db.getPassword())){
            if(!conn.isValid(0)) {
                System.out.println("Unable to connect to: " + db.getHost());
                System.exit(0);
            }

            PreparedStatement selectStatement = conn.prepareStatement("select from customer where customer.cpf = " + "'" +cpf+"'" );
            ResultSet rs = selectStatement.executeQuery();

            while(rs.next()) {
                String name = rs.getString("name");
                System.out.println(name);
                String token = cognitoAutenticator.getToken();
                response.setBody(token);
            }
        }

        return response;
    }
}

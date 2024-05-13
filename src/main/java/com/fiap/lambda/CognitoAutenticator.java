package com.fiap.lambda;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CognitoAutenticator {
    private final String cognito_token_endpoint;
    private final String client_id;
    private final String client_secret;
    private final String grant_type;

    public CognitoAutenticator() {
        this.cognito_token_endpoint = System.getenv("COGNITO_TOKEN_ENDPOINT");
        this.client_id = System.getenv("CLIENT_ID");
        this.client_secret = System.getenv("CLIENT_SECRET");
        this.grant_type = System.getenv("GRANT_TYPE");
    }

    public CognitoAutenticator(String cognito_token_endpoint, String client_id, String client_secret, String grant_type) {
        this.cognito_token_endpoint = cognito_token_endpoint;
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.grant_type = grant_type;
    }

    public String getToken() {
        try {
            // Codificar o client_id e client_secret para o formato Base64
            String credentials = client_id + ":" + client_secret;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            // Criar a conexão HTTP
            URL url = new URL(cognito_token_endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Authorization", "Basic " + encodedCredentials);

            // Parâmetros da solicitação
            String urlParameters = "grant_type=" + grant_type;

            // Enviar a solicitação
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            // Ler a resposta

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Transformar a resposta em um objeto JSON
            JSONObject jsonResponse = new JSONObject(response.toString());

            return jsonResponse.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

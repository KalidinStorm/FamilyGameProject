package ClientSide;

import Shared.Serializer;

import java.io.*;
import java.net.*;

import java.util.Map;




public class ServerConnector {

    private static final int TIMEOUT_MILLIS = 60000;
    int portNumber = 8000;
    private final String host = "http://localhost:" + portNumber; //todo put the ip address or url of the host here
    Serializer serializer;

    private interface RequestStrategy {
        void setRequestMethod(HttpURLConnection connection) throws IOException;
        void sendRequest(HttpURLConnection connection) throws IOException;
    }

    ServerConnector(Serializer serializer){
        this.serializer = serializer;
    }


    <T> T doPost(String urlPath, final Object requestInfo, Map<String, String> headers, Class<T> returnType) throws IOException {

        RequestStrategy requestStrategy = new RequestStrategy() {
            @Override
            public void setRequestMethod(HttpURLConnection connection) throws IOException {
                connection.setRequestMethod("POST");
            }

            @Override
            public void sendRequest(HttpURLConnection connection) throws IOException {
                connection.setDoOutput(true);

                String entityBody = serializer.serialize(requestInfo);
                try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                    os.writeBytes(entityBody);
                    os.flush();
                }
            }
        };
        return doRequest(urlPath, headers, returnType, requestStrategy);
    }


    private <T> T doRequest(String urlPath, Map<String, String> headers, Class<T> returnType, RequestStrategy requestStrategy)
            throws IOException {

        HttpURLConnection connection = null;

        try {
            URL url = getUrl(urlPath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIMEOUT_MILLIS);
            requestStrategy.setRequestMethod(connection);

            if(headers != null) {
                for (String headerKey : headers.keySet()) {
                    connection.setRequestProperty(headerKey, headers.get(headerKey));
                }
            }

            requestStrategy.sendRequest(connection);

            switch (connection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    String responseString = getResponse(connection.getInputStream());
                    return serializer.deserialize(responseString, returnType);
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    //todo handle
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    //todo handle
                default:
                    throw new RuntimeException("An unknown error occurred. Response code = " + connection.getResponseCode());
            }
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    private String getResponse(InputStream inputStream) throws IOException {
        if(inputStream == null)  {
            return null;
        } else {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {

                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                return response.toString();
            }
        }
    }

    private URL getUrl(String urlPath) throws MalformedURLException {
        String urlString = host + (urlPath.startsWith("/") ? "" : "/") + urlPath;
        return new URL(urlString);
    }

}


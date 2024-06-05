package org.request;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmailRequest {

    // getEmail, setEmail 관련 POST 리퀘스트 구현

    public String getEmail() throws IOException {
        URL url = new URL("http://[서버 주소]/getEmail"); // 서버 주소 입력
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST"); // POST 방식으로 설정

        int responseCode = connection.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return "Your Email: " + response.toString(); // 서버 응답 반환
    }

    public String setEmail(String email) throws IOException {
        String urlParameters = "email=" + email; // 파라미터 생성
        byte[] postData = urlParameters.getBytes("UTF-8"); // 파라미터 인코딩

        URL url = new URL("http://[서버 주소]/setEmail"); // 서버 주소 입력
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", Integer.toString(postData.length));

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(postData);
        wr.flush();
        wr.close();

        int responseCode = connection.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString(); // 서버 응답 반환
    }
}

package org.request;

import org.util.Hash;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmailRequest {

    // getEmail, setEmail 관련 POST 리퀘스트 구현

    public String getEmail() throws IOException {
        String urlParameters = ""; // 서버에 전송할 파라미터
        String urlString = "https://localhost:8443/users/getEmail";
        HttpsRequest request = new HttpsRequest();

        return "Your Email: " + request.requestHttps(urlString,urlParameters); // 서버 응답 반환
    }

    public String setEmail(String email) throws IOException {
        Hash hash = new Hash();
        String hashedEmail = hash.getSHA256Hash(email);
        String urlParameters = "email=" + email + "&hashedEmail=" + hashedEmail; // 파라미터 생성
        String urlString = "https://localhost:8443/users/setEmail";
        HttpsRequest request = new HttpsRequest();
        return request.requestHttps(urlString,urlParameters); // 서버 응답 반환
    }
}

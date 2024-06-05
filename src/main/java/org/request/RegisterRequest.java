package org.request;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterRequest {

    // 회원가입 관련 POST 방식 리퀘스트 구현
    public String register(String id, String pw, String email) throws IOException {
        String urlParameters = "id=" + id + "&password=" + pw + "&email=" + email; // 파라미터 생성
        String urlString = "https://localhost:8443/users/addUser";
        HttpsRequest request = new HttpsRequest();
        return request.requestHttps(urlString,urlParameters);
    }
}

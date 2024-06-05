package org.request;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginRequest {

    // 로그인 관련 POST 방식 리퀘스트 구현
    public String login(String id, String pw) throws IOException {
        String urlParameters = "id=" + id + "&password=" + pw; // 서버에 전송할 파라미터
        String urlString = "https://localhost:8443/users/login";
        HttpsRequest request = new HttpsRequest();
        return request.requestHttps(urlString,urlParameters);
    }
}

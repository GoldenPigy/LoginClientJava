package org.request;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.List;
import java.util.Map;

public class HttpsRequest
{
    String keystorePath = "C:/Users/yjpark0307/Desktop/keystore.jks";
    String keystorePassword = "lakezoo";

    static String cookie = "";

    public String requestHttps(String urlString,String urlParameters)
    {
        try {
            // 키스토어 로드
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

            // 키매니저 팩토리 초기화
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

            // 트러스트매니저 팩토리 초기화
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // SSL 컨텍스트 초기화
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            // 호스트 이름 검증 비활성화
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return hostname.equals("localhost");
                }
            });

            // 서버 URL 설정
            URL url = new URL(urlString); // HTTPS 서버 URL 및 엔드포인트
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // SSL 컨텍스트 설정
            connection.setSSLSocketFactory(sslContext.getSocketFactory());

            // POST 요청 설정
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            // 쿠키 설정
            if (cookie != null && !cookie.isEmpty()) {
                connection.setRequestProperty("Cookie", cookie);
            }

            // 요청 보내기
            try (OutputStream os = connection.getOutputStream()) {
                os.write(urlParameters.getBytes());
                os.flush();
            }

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 응답 읽기
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 세션 쿠키 확인
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            List<String> cookiesHeader = headerFields.get("Set-Cookie");
            if (cookiesHeader != null) {
                for (String cookieTemp : cookiesHeader) {
                    System.out.println("Cookie: " + cookieTemp);
                    cookie = cookieTemp;
                }
            }
            // 응답 출력
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}

package org.request;

public class EmailRequest {

    //getEmail, setEmail 관련 POST 리퀘스트 구현

    public String getEmail() {
        //이메일 정보를 백에서 받아와 email 변수에 대입하고 성공했다면 success를 리턴
        //세션유지가 되기에 따로 입력값은 필요 없음
        //getEmail 요청 사용
        String result = "example@jbnu.ac.kr"; // "example@jbnu.ac.kr" 대신에 받아온 result값을 출력
        return "Your Email: " + result;
    }

    public String setEmail(String email) {
        //유저에게서 받은 email 스트링을 백으로 보내고 result값을 받아옴
        //이후 클래스 속성에 대입
        //setEmail 요청 사용
        String result = "success"; // "success" 대신 받아온 result값을 출력
        return result;
    }

}

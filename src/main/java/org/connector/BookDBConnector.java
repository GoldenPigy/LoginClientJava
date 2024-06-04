package org.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class BookDBConnector
{
    DBConnector dbConnector = new DBConnector();
    String queryString;
    String result;
    int bookNum;
    String bookName;
    String bookWriter;
    String bookPublisher;
    String[][] bookInfo;

    //임시 아이디. 나중에 null로 처리
    private static String userId = null;

    public static boolean isLoggedIn() {return !(userId == null);}

    public static void loginSuccess(String id)
    {
        userId = id;
    }

    public Object[][] searchBookRequest(String name, String writer, String publisher) throws UnsupportedEncodingException {
        queryString = "/search?name=" + URLEncoder.encode(name, StandardCharsets.UTF_8.toString()) +
                "&writer=" + URLEncoder.encode(writer, StandardCharsets.UTF_8.toString()) +
                "&publisher=" + URLEncoder.encode(publisher, StandardCharsets.UTF_8.toString());
        result = dbConnector.connectDB(queryString);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(result);
            bookInfo = new String[rootNode.size()][4];
            for (int i = 0; i < rootNode.size(); i++) {
                JsonNode singleNode = rootNode.get(i);

                bookNum = singleNode.path("num").asInt();
                bookName = singleNode.path("name").asText();
                bookWriter = singleNode.path("writer").asText();
                bookPublisher = singleNode.path("publisher").asText();
                String[] singleBookInfo = {String.valueOf(bookNum), bookName, bookWriter, bookPublisher};
                bookInfo[i] = singleBookInfo;
            }
            System.out.println(bookInfo[1][2]);
            return bookInfo;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error");
            return null;
        }
    }

    public String borrowBookRequest(int num)
    {
        if(userId==null)
        {
            return "please login";
        }
        queryString = "/bookBorrow?num=" + num + "&member=" + userId;
        result = dbConnector.connectDB(queryString);
        return result;
    }

    public String returnBookRequest(int num)
    {
        if(userId==null)
        {
            return "please login";
        }
        queryString = "/bookReturn?num=" + num;
        result = dbConnector.connectDB(queryString);
        return result;
    }

    public String requestBookRequest(String date, String name, String writer, String explain)
    {
        if(userId==null)
        {
            return "please login";
        }
        queryString = "/request?date=" + date + "&name=" + name + "&writer=" + writer + "&explain=" + explain;
        result = dbConnector.connectDB(queryString);
        return result;
    }

    public Object[][] borrowBookListRequest()
    {
        if(userId==null)
        {
            return null;
        }
        System.out.println(queryString);
        queryString = "/memberBookBorrowList?id=" + userId;
        result = dbConnector.connectDB(queryString);
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            JsonNode rootNode=objectMapper.readTree(result);
            bookInfo=new String[rootNode.size()][4];
            for(int i=0;i<rootNode.size();i++)
            {
                JsonNode singleNode = rootNode.get(i);
                bookNum=singleNode.path("num").asInt();
                bookName=singleNode.path("name").asText();
                bookWriter=singleNode.path("writer").asText();
                bookPublisher=singleNode.path("publisher").asText();
                String[] singleBookInfo= {String.valueOf(bookNum),bookName,bookWriter,bookPublisher};
                bookInfo[i]=singleBookInfo;
            }
            return bookInfo;
        } catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("error");
            return null;
        }
    }
    public Object[][] getBestBookRequest()
    {
        queryString = "/bestBook";
        result = dbConnector.connectDB(queryString);
        System.out.println(result);
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            JsonNode rootNode=objectMapper.readTree(result);
            System.out.println(rootNode.size());
            bookInfo=new String[rootNode.size()][5];
            for(int i=0;i<rootNode.size();i++)
            {
                JsonNode singleNode = rootNode.get(i);
                int bookRank = i+1;
                bookNum=singleNode.path("num").asInt();
                bookName=singleNode.path("name").asText();
                bookWriter=singleNode.path("writer").asText();
                bookPublisher=singleNode.path("publisher").asText();
                String[] singleBookInfo= {String.valueOf(bookRank),String.valueOf(bookNum),bookName,bookWriter,bookPublisher};
                bookInfo[i]=singleBookInfo;
            }
            return bookInfo;
        } catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("error");
            return null;
        }
    }




}

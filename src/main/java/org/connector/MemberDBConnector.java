package org.connector;

public class MemberDBConnector
{
    DBConnector dbConnector = new DBConnector();
    String queryString,result;

    public String joinMemeberRequest(String id,String password, String name)
    {
        queryString = "/memberJoin?id=" + id + "&password=" + password + "&name=" + name;
        result = dbConnector.connectDB(queryString);
        return result;
    }
    public String loginMemberRequest(String id,String password)
    {
        queryString = "/memberLogin?id=" + id + "&password=" + password;
        result = dbConnector.connectDB(queryString);
        if(result.equals("log in success"))
        {
            BookDBConnector.loginSuccess(id);
        }
        return result;
    }
}
package com.test.demo;

import com.test.demo.util.DBUtil;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import spark.Spark;
import java.io.IOException;


import static spark.Spark.*;

public class Main {
    public static void main(String[] args) throws IOException {
        port(7000);
        get("/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            String html = DBUtil.selectOne(id, args[0]).getCodeAns();
            String rpd = html.replace("https://csdnimg.cn/release/blogv2/dist/mdeditor/css/editerView/", "http://localhost:7000/css/")
                    .replace("https://img-blog.csdnimg.cn/", "http://localhost:7000/png/");

            return rpd;
        });

        get("/css/:fileName", (request, response) -> {
            String fileName = request.params(":fileName");
            return doHttpGet("https://csdnimg.cn/release/blogv2/dist/mdeditor/css/editerView/" + fileName);
        });


        get("/png/:fileName", (request, response) -> {
            String fileName = request.params(":fileName");
            response.header("Content-Type", "image/png");
            return doHttpGetByte("https://img-blog.csdnimg.cn/" + fileName);
        });


        Spark.awaitInitialization();

    }


    public static String doHttpGet(String url) {
        String result = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response =httpClient.execute(httpGet)){
                int statusCode = response.getCode();
                if (HttpStatus.SC_OK != statusCode) {
                    httpGet.abort();
                    throw new RuntimeException("HttpClient,error status code :" + statusCode);
                }
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    result = EntityUtils.toString(entity);
                }
                EntityUtils.consume(entity);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static byte[] doHttpGetByte(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response =httpClient.execute(httpGet)){
                int statusCode = response.getCode();
                if (HttpStatus.SC_OK != statusCode) {
                    httpGet.abort();
                    throw new RuntimeException("HttpClient,error status code :" + statusCode);
                }
                HttpEntity entity = response.getEntity();
                byte[] result = null;
                if (null != entity) {
                    result = EntityUtils.toByteArray(entity);
                }
                EntityUtils.consume(entity);
                return result;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}

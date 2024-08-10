package com;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Scanner;

public class ChatGPT {


    private static final String API_URL = "https://api.chatanywhere.tech/v1/chat/completions";

    private static final String API_KEY = System.getenv("OPENAI_API_KEY");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("你想问什么？ (输入 'exit' 退出)");

        while (true) {
            String question = scanner.nextLine();

            if (question.equalsIgnoreCase("exit")) {
                break;
            }


            String response = sendRequest(question);
            System.out.println("回复: " + response);
        }

        scanner.close();
    }


    private static String sendRequest(String question) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(API_URL);

            // 设置请求头中的Content-Type和授权信息
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + API_KEY);

            // 构造请求体JSON字符串
            String json = String.format(
                    "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"temperature\": 0.7}",
                    question
            );
            httpPost.setEntity(new StringEntity(json));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseString = EntityUtils.toString(response.getEntity());

                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(responseString);

                // 获取回复内容
                String content = rootNode
                        .path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();

                return content.trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "发生错误: " + e.getMessage();
        }
    }
}
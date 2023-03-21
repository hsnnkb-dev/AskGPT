package dev.hsn;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class AskGPT {

    public static void main(String[] args) throws IOException, InterruptedException {
        String prompt;
        if(args.length > 0) {
            prompt = args[0];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("What would you like to ask?");
            prompt = scanner.nextLine();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ChatGPTRequest chatGptRequest = new ChatGPTRequest( "text-davinci-001", prompt, 1, 100);
        String input = objectMapper.writeValueAsString(chatGptRequest);

        Dotenv dotenv = Dotenv.load();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + dotenv.get("OPENAI-API-KEY"))
                .POST(HttpRequest.BodyPublishers.ofString(input))
                .build();

        HttpClient client =  HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200) {
            ChatGPTResponse chatGptResponse = objectMapper.readValue(response.body(), ChatGPTResponse.class);
            String answer = chatGptResponse.choices()[chatGptResponse.choices().length - 1].text();
            if (!answer.isEmpty()) {
                System.out.println(answer.replace("\n", "").trim());
            }
        } else {
            System.out.println(response.statusCode());
            System.out.println(response.body());
        }
    }
}

package edu.comp331;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;

public class Main {

    public static void main(String[] args) throws Exception {
        String prompt = String.join(" ", args);
        String key = Dotenv.configure().ignoreIfMissing().load()
                .get("GEMINI_API_KEY", System.getenv("GEMINI_API_KEY"));

        JSONObject body = new JSONObject().put("contents", new JSONArray()
                .put(new JSONObject().put("parts", new JSONArray()
                        .put(new JSONObject().put("text", prompt)))));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent"))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", key)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        String response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString()).body();

        System.out.println(new JSONObject(response)
                .getJSONArray("candidates").getJSONObject(0)
                .getJSONObject("content").getJSONArray("parts")
                .getJSONObject(0).getString("text"));
    }
}

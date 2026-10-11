package edu.comp331;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;

public class Main {

    public static void main(String[] args) throws Exception {
        boolean verbose = Arrays.asList(args).contains("--verbose");
        String prompt = Arrays.stream(args)
                .filter(arg -> !arg.equals("--verbose"))
                .collect(java.util.stream.Collectors.joining(" "));
        String model = "gemini-3.8-flash";

        if (prompt.isBlank()) {
            System.err.println("Usage: mvn exec:java -Dexec.args=\"[--verbose] your prompt\"");
            return;
        }

        if (verbose) {
            System.out.println("Model: " + model);
            System.out.println("Prompt: " + prompt);
        }
        String key = Dotenv.configure().ignoreIfMissing().load()
                .get("GEMINI_API_KEY", System.getenv("GEMINI_API_KEY"));

        if (key == null || key.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY was not found.");
        }

        JSONObject body = new JSONObject().put("contents", new JSONArray()
                .put(new JSONObject().put("parts", new JSONArray()
                        .put(new JSONObject().put("text", prompt)))));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent"))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", key)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Gemini API error: " + response.body());
        }

        JSONObject result = new JSONObject(response.body());

        if (verbose && result.has("usageMetadata")) {
            JSONObject usage = result.getJSONObject("usageMetadata");
            System.out.println("Prompt tokens: " + usage.optInt("promptTokenCount"));
            System.out.println("Response tokens: " + usage.optInt("candidatesTokenCount"));
            System.out.println("Total tokens: " + usage.optInt("totalTokenCount"));
        }

        System.out.println(result.getJSONArray("candidates").getJSONObject(0)
                .getJSONObject("content").getJSONArray("parts")
                .getJSONObject(0).getString("text"));
    }
}

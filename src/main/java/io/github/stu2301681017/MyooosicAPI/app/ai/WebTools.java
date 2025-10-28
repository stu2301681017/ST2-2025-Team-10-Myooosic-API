package io.github.stu2301681017.MyooosicAPI.app.ai;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class WebTools {

    // morality is subjective
    private final String userAgentHeader = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36";
    private final WebClient webClient;

    public WebTools(
    ) {
        this.webClient = WebClient.builder().build();
    }

    @Tool(description = "Search Google and return the raw HTML of the results page.")
    public String searchGoogle(@ToolParam(description = "Query string to search on Google") String query) {

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://www.google.com/search?q=" + encodedQuery;

        return webClient.get()
                .uri(url)
                .header("User-Agent", userAgentHeader)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Tool(description = "Fetch any URL and return the raw content as a string.")
    public String fetchUrl(
            @ToolParam(description = "Full URL to fetch") String url) {

        return webClient.get()
                .uri(url)
                .header("User-Agent", userAgentHeader)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}

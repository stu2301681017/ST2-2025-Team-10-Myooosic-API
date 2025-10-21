package io.github.stu2301681017.MyooosicAPI.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ObjectMapper objectMapper;

    public AiService(
        ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    public String promptText(Prompt prompt) {
        final String aiResponse = "";
        return aiResponse;
    }

    public <T> T promptClass(Prompt prompt, Class<T> targetType) {
        final String aiResponse = "";
        try {
            String json = objectMapper.writeValueAsString(aiResponse);
            return objectMapper.readValue(json, targetType);
        } catch (JsonProcessingException e) {
            throw new BadAIJsonException("Failed to convert PromptRecord to JSON", e);
        }
    }


}

package io.github.stu2301681017.MyooosicAPI.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ObjectMapper objectMapper;

    public AiService(
        ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    public String promptText(@NotNull Prompt prompt) {
        final String aiResponse = "";
        return aiResponse;
    }

    public <T> T promptClass(@NotNull Prompt prompt, @NotNull Class<T> targetType) {
        final String jsonCommand = """
            
             
            Don't write ANYTHING else except your result in the following
            JSON format: (the fields are filled with default values)
            
                """;
        String jsonCommandFilled = jsonCommand;
        try {
            jsonCommandFilled += objectMapper.writeValueAsString(targetType.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new BadAIJsonException(targetType.getName() + " cannot be converted to AI Json", e);
        }
        final String query = prompt.text() + jsonCommandFilled;
        // send to ai
        final String aiResponse = "";
        try {
            String json = objectMapper.writeValueAsString(aiResponse);
            return objectMapper.readValue(json, targetType);
        } catch (JsonProcessingException e) {
            throw new BadAIJsonException("Failed to cast AI Json to " + targetType.getName(), e);
        }
    }


}

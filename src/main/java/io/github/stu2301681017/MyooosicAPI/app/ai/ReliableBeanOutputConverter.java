package io.github.stu2301681017.MyooosicAPI.app.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.converter.StructuredOutputConverter;

public class ReliableBeanOutputConverter<T> implements StructuredOutputConverter<T> {

    /**
     * This right here is the EXACT reason why working with AI, specifically a
     * locally hosted one, is nothing short of absolute cancer. Please, do not.
     */
    private final String command = """
    You are a strict JSON generator. You MUST follow these rules:
    
    1. ONLY output JSON. No explanations, no commentary, no extra text, no markdown, nothing else.
    2. Your output must strictly conform to the following JSON example:
    
    JSONEXAMPLE
    
    3. All required fields must be present.
    4. The JSON must be parseable by a standard JSON parser.
    5. DO NOT add quotes, special characters, or formatting that breaks JSON.
    6. DO NOT add random or invalid data in the fields.
    7. If you don't know what to add or didn't find data, JUST KEEP THE ARRAY [] EMPTY!!!
    8. DO NOT EVER ADD INCOMPLETE OR INVALID PROPERTIES, ONLY EMPTY ARRAYS!
    9. NEVER add random or invalid data, instead keep the array empty. Especially if you're not absolutely sure about your output.
    10. DO NOT EVER add the example that you are given. It is ONLY an example of what proper output looks like.
    11. DO NOT EVER add the example that you are given. It is ONLY an example of what proper output looks like.
    12. DO NOT EVER add the example that you are given. It is ONLY an example of what proper output looks like.
    
    13. DO NOT EVER ADD RANDOM, INVALID OR EMPTY DATA IN PROPERTIES, KEEP ARRAY EMPTY
    14. DO NOT EVER ADD RANDOM, INVALID OR EMPTY DATA IN PROPERTIES, KEEP ARRAY EMPTY
    15. DO NOT EVER ADD RANDOM, INVALID OR EMPTY DATA IN PROPERTIES, KEEP ARRAY EMPTY
    
    Generate a single valid JSON object that strictly follows the example above.
    """;

    private final String format;
    private final Class<T> targetType;
    private final ObjectMapper mapper;

    public ReliableBeanOutputConverter(Class<T> targetType, T example) {

        this.targetType = targetType;
        mapper = new ObjectMapper();

        try {
            String exampleJson = mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(example);

            format = command.replaceFirst("JSONEXAMPLE", exampleJson);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to initialize converter with given type", e);
        }

    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public T convert(String source) {
        System.out.println(source);
        try {
            return mapper.readValue(source, targetType);
        } catch (JsonProcessingException e) {
            throw new AIResponseException("AI responded with bad Json", e);
        }
    }

}

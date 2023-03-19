package dev.hsn;

public record ChatGPTResponse(
        String id,
        String object,
        int created,
        String model,
        ChatGPTResponseChoice[] choices,
        ChatGPTResponseUsage usage
) {
}

package com.bigobrains.ai.messaging.cases.management.chats;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
public class ChatsController {

    private final VectorStore vectorStore;
    private final ChatClient chatBotClient;

    private static final String ADVISE_USER = """
            Use the getCaseDetailsByCaseId function, to answer any questions, regarding the case details.
            Use the getMerchantAccountDetailsByMerchantId function, to answer any questions, regarding the merchant details.
            Use the sendEmailByEmailId function, to send any email communications.
            """;

    public ChatsController(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.chatBotClient = chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }

    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AssistantPrompt.class),
                    examples = @ExampleObject(
                            name = "AssistantPrompt",
                            value = "{\"message\": \"The role of the user axbadri@gmail.com is ANONYMOUS_USER. According to the provided context, only users with an ADMIN role can perform system changes, while users with an ANONYMOUS_USER role cannot perform any system changes.\"}"
                    )
            )
    )
    @PostMapping(value = "/chats")
    public AssistantPrompt postChat(@RequestBody UserPrompt userPrompt) {
        String userText = String.join("{context}\n", userPrompt.getMessage(), "\n{format}");
        ChatResponse response = chatBotClient.prompt()
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
                .options(AzureOpenAiChatOptions.builder().withFunctions(Set.of("getSolicitorRoleByEmailId", "sendEmailByEmailId", "getMerchantStatusByMerchantId", "getMerchantDetailsByMerchantId", "getCaseDetailsByCaseId")).build())
                .user(u -> u.text(userText).params(Map.of(
                        "context", ADVISE_USER,
                        "format", "Please provide the results in simple, everyday language that is easy to understand and free of technical jargon. Use straightforward sentences and provide examples where appropriate to make the content more relatable."
                )))
                .call().chatResponse();
        return new AssistantPrompt(response.getResult().getOutput().getContent());
    }
}

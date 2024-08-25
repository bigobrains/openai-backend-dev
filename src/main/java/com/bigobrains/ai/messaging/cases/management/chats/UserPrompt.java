package com.bigobrains.ai.messaging.cases.management.chats;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrompt {

    private @Schema(example = "What is the role of the user axbadri@gmail.com") String message;
}

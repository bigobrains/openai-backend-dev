package com.bigobrains.ai.messaging.cases.management.users;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class),
                    examples = @ExampleObject(
                            name = "User",
                            value = "[{\"id\": \"41497579\", \"name\": \"Arvind Badri\", \"emailId\": \"barvind88@gmail.com\", \"role\": \"ADMIN\", \"title\": \"Lab Director\", \"companyName\": \"United Diagnostics\", \"zipCode\": \"53186\"}]"
                    )
            )
    )
    @GetMapping(value = "/users")
    public ResponseEntity<?> getUsers(@RequestParam(value = "emailId", required = false) String emailId) {
        if (emailId != null && !emailId.isEmpty()) {
            return new ResponseEntity<>(userRepository.findByEmailId(emailId), HttpStatus.OK);
        }
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }
}

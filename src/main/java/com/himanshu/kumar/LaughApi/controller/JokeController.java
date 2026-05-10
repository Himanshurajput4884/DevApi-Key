package com.himanshu.kumar.LaughApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Joke Generator", description = "Endpoint for generating random jokes")
public class JokeController {

    private final JokeGenerator jokeGenerator;

    @GetMapping(value = "/joke", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Generate a random jokes")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="Successfully generated random jokes",
            headers = @Header(name="X-Rate-Limit", description = "The number of remaining API invocations available with the user.", required=true,
            schema = @Schema(type="integer"))),
            @ApiResponse(responseCode = "429", description="API Rate Limit exhausted",
            headers = @Header(name = "X-Rate-Limit-Retry-After-Seconds", description = "Wait perriod in seconds before the user can invoke the API endpoint", required = true,
            schema = @Schema(type="integer")),
            content = @Content(schema = @Schema(implementation = ExceptionResponseDto.class)))
    })
    public ResponseEntity<JokeResponseDto> generate() {
        final var response = jokeGenerator.generate();
        return ResponseEntity.ok(response);
    }
}

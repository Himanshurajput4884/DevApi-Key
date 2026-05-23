package com.himanshu.kumar.LaughApi.utility;

import com.himanshu.kumar.LaughApi.dto.JokeResponseDto;
import net.datafaker.Faker;
import net.datafaker.providers.entertainment.Joke;
import org.springframework.stereotype.Component;

/**
 * Utility class for generating random jokes
 */
@Component
public class JokeGenerator {

    private final Joke joke = new Faker().joke();

    /**
     * Generates a random joke.
     */
    public JokeResponseDto generate() {
        final var pun = joke.pun();

        return JokeResponseDto.builder()
                .joke(pun)
                .build();
    }
}
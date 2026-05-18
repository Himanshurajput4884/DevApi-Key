package com.himanshu.kumar.LaughApi.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.himanshu.kumar.LaughApi.dto.ExceptionResponseDto;
import com.himanshu.kumar.LaughApi.service.RateLimitingService;
import com.himanshu.kumar.LaughApi.utility.ApiEndpointSecurityInspector;
import com.himanshu.kumar.LaughApi.utility.AuthenticatedUserIdProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String RATE_LIMIT_REMAINING_HEADER = "X-Rate-Limit";
    private static final String RATE_LIMIT_RETRY_AFTER_HEADER = "X-Rate-Limit-Retry-After-Seconds";
    private static final String RATE_LIMIT_EXHAUSTED_MESSAGE = "API rate limit exhausted. Please try again later.";

    private final ObjectMapper objectMapper;
    private final RateLimitingService rateLimitingService;
    private final AuthenticatedUserIdProvider authenticatedUserIdProvider;
    private final ApiEndpointSecurityInspector apiEndpointSecurityInspector;

    @Override
    @SneakyThrows
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain) {
        if (apiEndpointSecurityInspector.isUnsecureRequest(request) || !authenticatedUserIdProvider.isAvailable()) {
            filterChain.doFilter(request, response);
            return;
        }

        final var userId = authenticatedUserIdProvider.getUserId();
        final var bucket = rateLimitingService.getBucket(userId);
        final var consumptionProbe = bucket.tryConsumeAndReturnRemaining(1);

        if (consumptionProbe.isConsumed()) {
            response.setHeader(RATE_LIMIT_REMAINING_HEADER, String.valueOf(consumptionProbe.getRemainingTokens()));
            filterChain.doFilter(request, response);
            return;
        }

        final var retryAfterSeconds = TimeUnit.NANOSECONDS.toSeconds(consumptionProbe.getNanosToWaitForRefill());
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader(RATE_LIMIT_RETRY_AFTER_HEADER, String.valueOf(retryAfterSeconds));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(prepareErrorResponseBody());
    }

    @SneakyThrows
    private String prepareErrorResponseBody() {
        final var exceptionResponse = new ExceptionResponseDto<String>();
        exceptionResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.toString());
        exceptionResponse.setDescription(RATE_LIMIT_EXHAUSTED_MESSAGE);
        return objectMapper.writeValueAsString(exceptionResponse);
    }
}

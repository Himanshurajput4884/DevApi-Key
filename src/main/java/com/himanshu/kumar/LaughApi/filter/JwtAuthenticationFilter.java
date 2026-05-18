package com.himanshu.kumar.LaughApi.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.himanshu.kumar.LaughApi.dto.ExceptionResponseDto;
import com.himanshu.kumar.LaughApi.utility.ApiEndpointSecurityInspector;
import com.himanshu.kumar.LaughApi.utility.JwtUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/***
 * JwtAuthenticationFilter is a custom filter registered with the spring
 * security filter chain and works in conjunction with the security
 * configuration
 */


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final JwtUtility jwtUtility;
    private final ApiEndpointSecurityInspector apiEndpointSecurityInspector;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String MISSING_TOKEN_ERROR_MESSAGE = "Authentication failure: Token missing, invalid or expired";

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        final var unsecuredApiBeingInvoked = apiEndpointSecurityInspector.isUnsecureRequest(request);

        if(Boolean.FALSE.equals(unsecuredApiBeingInvoked)) {
            final var authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

            if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
               final var token = authorizationHeader.replace(BEARER_PREFIX, "");

               final var userId = jwtUtility.getUserId(token);
               final var authentication = new UsernamePasswordAuthenticationToken(userId, null, null);
               authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
               SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                setAuthErrorDetails(response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Set the authentication detail in Error response
     * @param httpServletResponse
     */

    @SneakyThrows
    private void setAuthErrorDetails(HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        final var errorResponse = prepareErrorResponseBody();
        httpServletResponse.getWriter().write(errorResponse);
    }


    /**
     * return JSON representation of the invalid token error response body
     * @return
     */
    @SneakyThrows
    private String prepareErrorResponseBody() {
        final var exceptionResponse = new ExceptionResponseDto<String>();
        exceptionResponse.setStatus(HttpStatus.UNAUTHORIZED.toString());
        exceptionResponse.setDescription(MISSING_TOKEN_ERROR_MESSAGE);
        return objectMapper.writeValueAsString(exceptionResponse);
    }
}

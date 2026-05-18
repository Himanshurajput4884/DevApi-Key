package com.himanshu.kumar.LaughApi.utility;

import com.himanshu.kumar.LaughApi.configuration.OpenApiConfigurationProperties;
import com.himanshu.kumar.LaughApi.configuration.PublicEndpoint;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Utility class responsible for evaluating the accessibility of API endpoints
 * based on their security configuration. It works in conjunction with the mappings
 * of controller methods
 */

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(OpenApiConfigurationProperties.class)
public class ApiEndpointSecurityInspector {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final OpenApiConfigurationProperties openApiConfigurationProperties;
    private static final List<String> SWAGGER_V3_PATHS = List.of("/swagger-ui**/**", "/v3/api-docs**/**");

    @Getter
    private List<String> publicGetEndpoints = new ArrayList<>();
    @Getter
    private List<String> publicPostEndpoints = new ArrayList<>();

    /**
     * Initializes the class by gathering public endpoints for various HTTP methods.
     * It identifies designated Public endpoints within the application's mappings
     * and adds them to separate lists based on their associated HTTP methods
     * If OpenAPI is enabled, Swagger endpoints are also considered as public
     */
    @PostConstruct
    public void init() {
        final var handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        handlerMethods.forEach((requestInfo, handlerMethod) -> {
            if (handlerMethod.hasMethodAnnotation(PublicEndpoint.class)) {
                final var httpMethod = requestInfo.getMethodsCondition().getMethods().iterator().next().asHttpMethod();
                final var apiPaths = requestInfo.getPathPatternsCondition().getPatternValues();

                if(httpMethod.equals(HttpMethod.GET)) {
                    publicGetEndpoints.addAll(apiPaths);
                } else {
                    publicPostEndpoints.addAll(apiPaths);
                }
            }
        });

        final var openApiEnabled = openApiConfigurationProperties.getOpenApi().isEnabled();
        if (Boolean.TRUE.equals(openApiEnabled)) {
            publicGetEndpoints.addAll(SWAGGER_V3_PATHS);
        }
    }

    /**
     * Checks if the provided HTTP request is directed towards an unsecured API endpoint
     * @param request
     * @return
     */
    public boolean isUnsecureRequest(@NonNull final HttpServletRequest request) {
        final var requestHttpMethod = HttpMethod.valueOf(request.getMethod());
        var unsecureApiPaths = getUnsecureApiPaths(requestHttpMethod);
        unsecureApiPaths = Optional.ofNullable(unsecureApiPaths).orElseGet(ArrayList::new);

        return unsecureApiPaths.stream().anyMatch(apiPath -> new AntPathMatcher().match(apiPath, request.getRequestURI()));
    }

    /**
     * Retrieves the list of unsecured API paths based on provided HTTP method
     * @param method
     * @return
     */
    private List<String> getUnsecureApiPaths(@NonNull final HttpMethod method) {
        if (HttpMethod.GET.equals(method)) {
            return publicGetEndpoints;
        }
        if (HttpMethod.POST.equals(method)) {
            return publicPostEndpoints;
        }
        return Collections.emptyList();
    }
}

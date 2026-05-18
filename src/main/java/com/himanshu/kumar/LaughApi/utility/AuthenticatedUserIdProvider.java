package com.himanshu.kumar.LaughApi.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Utility class dedicated to provide authenticated user's ID as stored in the
 * DataSource in UUID format which uniquely identifies the user in the system
 */

@Component
public class AuthenticatedUserIdProvider {

    /**
     * Retrieves ID corresponding to the authenticated user from the security
     * @return
     */
    public UUID getUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(UUID.class::isInstance)
                .map(UUID.class::cast)
                .orElseThrow(IllegalStateException::new);
    }

    /**
     * Checks whether the security context is populated with a valid authentication
     * object.
     * @return
     */
    public boolean isAvailable() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication).isPresent();
    }
}

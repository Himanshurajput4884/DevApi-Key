package com.himanshu.kumar.LaughApi.service;

import com.himanshu.kumar.LaughApi.repository.UserPlanMappingRepository;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RateLimitingService {

    private final ProxyManager<UUID> proxyManager;
    private final UserPlanMappingRepository userPlanMappingRepository;

    /**
     * Retrieves the stored rate-limiting bucket for the specified user. If no
     * bucket is found for the user, a new one is created and stored in the
     * provisioned cache based on the user's current subscription plan
     * @param userId
     * @return
     */
    public Bucket getBucket(@NonNull final UUID userId) {
        return proxyManager.builder().build(userId, () -> createBucketConfiguration(userId));
    }

    /**
     * Resets the rate limiting for the specified user-id
     * @param userId
     */
    public void reset(@NonNull final UUID userId) {
        proxyManager.removeProxy(userId);
    }

    /**
     * Constructs an instance of corresponding to the
     * user's active plan which enforce the allowed rate-limit of API invocation.
     * @param userId
     * @return
     */
    private BucketConfiguration createBucketConfiguration(@NonNull final UUID userId) {
        final var userPlanMapping = userPlanMappingRepository.getActivePlan(userId);
        final var limitPerHour = userPlanMapping.getPlan().getLimitPerHour();
        return BucketConfiguration.builder()
                .addLimit(limit -> limit.capacity(limitPerHour).refillIntervally(limitPerHour, Duration.ofHours(1)))
                .build();
    }
}

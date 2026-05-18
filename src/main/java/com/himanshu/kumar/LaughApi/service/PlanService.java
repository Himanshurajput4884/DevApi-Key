package com.himanshu.kumar.LaughApi.service;

import com.himanshu.kumar.LaughApi.dto.PlanResponseDto;
import com.himanshu.kumar.LaughApi.dto.PlanUpdationRequestDto;
import com.himanshu.kumar.LaughApi.entity.UserPlanMapping;
import com.himanshu.kumar.LaughApi.exception.InvalidPlanException;
import com.himanshu.kumar.LaughApi.repository.PlanRepository;
import com.himanshu.kumar.LaughApi.repository.UserPlanMappingRepository;
import com.himanshu.kumar.LaughApi.utility.AuthenticatedUserIdProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final RateLimitingService rateLimitingService;
    private final UserPlanMappingRepository userPlanMappingRepository;
    private final AuthenticatedUserIdProvider authenticatedUserIdProvider;

    /**
     * Updates the subscription plan for a user and deactivates their corrent plan
     * in the system. The rate-limit corresponding to the previous plan is cleared
     * on successfully plan updation.
     *
     * @param planUpdationRequest
     */
    public void update(@NonNull final PlanUpdationRequestDto planUpdationRequest) {
        final var planId =  planUpdationRequest.getPlanId();
        final var isPlanActive = planRepository.existsById(planId);
        if(Boolean.FALSE.equals(isPlanActive)) {
            throw new InvalidPlanException("No plan exists in the system with provided-id");
        }

        final var userId = authenticatedUserIdProvider.getUserId();
        final var isExistingUserPlan = userPlanMappingRepository.isActivePlan(userId, planId);
        if(Boolean.TRUE.equals(isExistingUserPlan)) {
            return;
        }

        userPlanMappingRepository.deactivateCurrentPlan(userId);

        final var newPlan = new UserPlanMapping();
        newPlan.setUserId(userId);
        newPlan.setPlanId(planId);
        userPlanMappingRepository.save(newPlan);

        rateLimitingService.reset(userId);
    }

    /**
     * Retrieves all available subscription plan
     * @return
     */
    public List<PlanResponseDto> retrieve() {
        return planRepository.findAll()
                .stream()
                .map(plan -> PlanResponseDto.builder()
                        .id(plan.getId())
                        .name(plan.getName())
                        .limitPerHour(plan.getLimitPerHour())
                        .build())
                .toList();
    }
}

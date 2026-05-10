package com.himanshu.kumar.LaughApi.repository;

import com.himanshu.kumar.LaughApi.entity.UserPlanMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserPlanMappingRepository extends JpaRepository<UserPlanMapping, UUID> {

    /**
     * Deactivates the current plan for the specified user.
     *
     * @param userId The unique identifier of the User
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = """
            UPDATE user_plan_mappings
            SET is_active = false
            WHERE user_id = ?1 and is_active = true
            """)
    void deactivateCurrentPlan(final UUID userId);



}

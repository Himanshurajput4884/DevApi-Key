package com.himanshu.kumar.LaughApi.repository;

import com.himanshu.kumar.LaughApi.entity.UserPlanMapping;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
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

    /**
     * Retrieves the active plan for the specified user.
     *
     * @param userId The unique identifier of the user
     * @return The active plan mapping for the user
     */
    @Query(nativeQuery = true, value = """
            SELECT * FROM user_plan_mappings
            WHERE user_id = ?1 AND is_active = true
            """)
    UserPlanMapping getActivePlan(final UUID userId);


    /**
    * Checks if the specified plan is active for the given user.
     * *
    * @param userId The unique identifier of the user
     * @param planId The unnique identifier of the plan
     * @return true if the plan is active for the user, false otherwise
    */

    @Query(value = """
            SELECT COUNT(id) = 1 FROM UserPlanMapping
            WHERE isActive = true
            AND userId = ?1
            AND planId = ?2
            """)
    boolean isActivePlan(final UUID userId, final UUID planId);

}

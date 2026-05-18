package com.himanshu.kumar.LaughApi.service;

import com.himanshu.kumar.LaughApi.dto.TokenSuccessResponseDto;
import com.himanshu.kumar.LaughApi.dto.UserCreationRequestDto;
import com.himanshu.kumar.LaughApi.dto.UserLoginRequestDto;
import com.himanshu.kumar.LaughApi.entity.User;
import com.himanshu.kumar.LaughApi.entity.UserPlanMapping;
import com.himanshu.kumar.LaughApi.exception.AccountAlreadyExistsException;
import com.himanshu.kumar.LaughApi.exception.InvalidLoginCredentialsException;
import com.himanshu.kumar.LaughApi.exception.InvalidPlanException;
import com.himanshu.kumar.LaughApi.repository.PlanRepository;
import com.himanshu.kumar.LaughApi.repository.UserPlanMappingRepository;
import com.himanshu.kumar.LaughApi.repository.UserRepository;
import com.himanshu.kumar.LaughApi.utility.JwtUtility;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtUtility jwtUtility;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPlanMappingRepository userPlanMappingRepository;

    /**
     * Creates a new user account in the system corresponding to provided
     * subscription plan in the request
     *
     * @param userCreationRequest
     */
    @Transactional
    public void create(@NonNull final UserCreationRequestDto userCreationRequest) {
        final var email = userCreationRequest.getEmail();
        final var userAccountExistsWithEmailId = userRepository.existsByEmailId(email);
        if(Boolean.TRUE.equals(userAccountExistsWithEmailId)) {
            throw new AccountAlreadyExistsException("Account with provided email-id already exists");
        }

        final var planId = userCreationRequest.getPlanId();
        final var isPlanIdValid = planRepository.existsById(planId);
        if (Boolean.FALSE.equals(isPlanIdValid)) {
           throw new InvalidPlanException("No plan exists in the system with provided-id");
        }

        final var user = new User();
        final var encodedPassword = passwordEncoder.encode(userCreationRequest.getPassword());
        user.setEmailId(email);
        user.setPassword(encodedPassword);
        final var savedUser = userRepository.save(user);

        final var userPlanMapping = new UserPlanMapping();
        userPlanMapping.setUserId(savedUser.getId());
        userPlanMapping.setPlanId(planId);
        userPlanMappingRepository.save(userPlanMapping);
    }

    /**
     * Validates User login credentials and generates an access token on successful authentication.
     *
     * @param userLoginRequest
     * @return
     */
    public TokenSuccessResponseDto login(@NonNull final UserLoginRequestDto userLoginRequest) {
        final var user = userRepository.findByEmailId(userLoginRequest.getEmail())
                .orElseThrow(InvalidLoginCredentialsException::new);

        final var encodedPassword = user.getPassword();
        final var plainTextPassword = userLoginRequest.getPassword();
        final var isCorrectPassword = passwordEncoder.matches(plainTextPassword, encodedPassword);
        if (Boolean.FALSE.equals(isCorrectPassword)) {
            throw new InvalidLoginCredentialsException();
        }

        final var accessToken = jwtUtility.generateAccessToken(user.getId());
        return TokenSuccessResponseDto.builder().accessToken(accessToken).build();
    }
}

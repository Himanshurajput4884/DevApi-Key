package com.himanshu.kumar.LaughApi.repository;

import com.himanshu.kumar.LaughApi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}

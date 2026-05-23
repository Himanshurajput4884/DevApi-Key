CREATE TABLE users
(
    id         BINARY(16)     NOT NULL,
    email_id   VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email_id (email_id)
);

CREATE TABLE plans
(
    id              BINARY(16)     NOT NULL,
    name            VARCHAR(255) NOT NULL,
    limit_per_hour  INT          NOT NULL,
    create_at       DATETIME(6)  NOT NULL,
    updated_at      DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_plans_name (name),
    UNIQUE KEY uk_plans_limit_per_hour (limit_per_hour)
);

CREATE TABLE user_plan_mappings
(
    id         BINARY(16)    NOT NULL,
    user_id    BINARY(16)    NULL,
    plan_id    BINARY(16)    NULL,
    is_active  TINYINT(1)  NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_upm_user_id (user_id),
    KEY idx_upm_plan_id (plan_id),
    CONSTRAINT fk_upm_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_upm_plan FOREIGN KEY (plan_id) REFERENCES plans (id)
);

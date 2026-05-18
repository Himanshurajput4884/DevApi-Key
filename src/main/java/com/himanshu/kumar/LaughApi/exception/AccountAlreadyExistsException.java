package com.himanshu.kumar.LaughApi.exception;

import io.micrometer.common.lang.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

public class AccountAlreadyExistsException extends ResponseStatusException {

    private static final long serialVersionUID = 7439642984069939024L;

    public AccountAlreadyExistsException(@NonNull final String reason) {
        super(HttpStatus.CONFLICT, reason);
    }
}


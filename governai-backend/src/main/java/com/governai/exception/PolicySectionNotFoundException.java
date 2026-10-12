package com.governai.exception;

import java.util.UUID;

public class PolicySectionNotFoundException extends RuntimeException {

    public PolicySectionNotFoundException(UUID id) {
        super("Policy section not found with id: " + id);
    }
}
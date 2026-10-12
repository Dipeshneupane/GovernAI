package com.governai.exception;

import java.util.UUID;

public class PolicyDocumentNotFoundException extends RuntimeException {

    public PolicyDocumentNotFoundException(UUID id) {
        super("Policy document not found with id: " + id);
    }
}
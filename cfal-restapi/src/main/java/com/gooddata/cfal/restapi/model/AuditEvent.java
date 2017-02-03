/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.cfal.restapi.model;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

/**
 * Audit event entity
 */
public class AuditEvent {

    @Id
    private ObjectId id;

    @NotNull
    private final String domain;

    @NotNull
    private final String userId;

    @NotNull
    private final DateTime timestamp;

    public AuditEvent(final String domain, final String userId, final DateTime timestamp) {
        this.domain = domain;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public ObjectId getId() {
        return id;
    }

    public String getDomain() {
        return domain;
    }

    public String getUserId() {
        return userId;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }
}
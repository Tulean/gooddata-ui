/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.cfal.account;

import com.gooddata.cfal.AbstractAT;
import com.gooddata.auditevent.AuditEvent;

import java.util.function.Predicate;

abstract class AbstractLoginAT extends AbstractAT {

    static final String MESSAGE_TYPE = "LOGIN";

    static final String WEBAPP = "WEBAPP";

    private static final String LOGIN_TYPE = "loginType";
    private static final String COMPONENT = "component";

    Predicate<AuditEvent> eventCheck(final boolean success, final String component, final String loginType) {
        return (e ->
                e.getUserLogin().equals(getAccount().getLogin()) &&
                        e.getType().equals(MESSAGE_TYPE) &&
                        e.isSuccess() == success &&
                        loginType.equals(e.getParams().get(LOGIN_TYPE)) &&
                        component.equals(e.getParams().get(COMPONENT)));
    }
}
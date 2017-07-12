/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.cfal;

import static com.gooddata.cfal.ETLDataloadProcessExecutionAuditLogEvent.ETL_SCHEDULE_CHANGE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import com.gooddata.context.ThreadContextUtils;
import com.gooddata.context.TransferableGdcCallContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ETLDataloadProcessExecutionAuditLogEventTest {

    private static final String USER_LOGIN = "bear@gooddata.com";
    private static final String USER_IP = "127.0.0.1";
    private static final String DOMAIN_ID = "default";
    private static final String PROJECT_ID = "FoodMartDemo";
    private static final String PROCESS = "/gdc/projects/" + PROJECT_ID + "/dataload/processes/TestProcessId";
    private static final String EXECUTION = PROCESS + "/executions/ExecutionId";
    private static final String INSTANCE = "/gdc/datawarehouse/instances/InstanceId";

    @Before
    public void setUp() {
        final TransferableGdcCallContext context = new TransferableGdcCallContext();
        context.setUserLogin(USER_LOGIN);
        context.setClientIp(USER_IP);
        context.setDomainId(DOMAIN_ID);
        context.setProjectId(PROJECT_ID);
        ThreadContextUtils.setUpGdcCallContext(context);
    }

    @After
    public void tearDown() {
        ThreadContextUtils.clearGdcCallContext();
    }

    @Test
    public void shouldSetValuesFromGdcCallContext() {
        final ETLDataloadProcessExecutionAuditLogEvent event = new ETLDataloadProcessExecutionAuditLogEvent(ETL_SCHEDULE_CHANGE, true, PROCESS, EXECUTION, INSTANCE);

        assertThat(event.getType(), is(ETL_SCHEDULE_CHANGE));
        assertThat(event.isSuccess(), is(true));
        assertThat(event.getUserLogin(), is(USER_LOGIN));
        assertThat(event.getUserIp(), is(USER_IP));
        assertThat(event.getDomainId(), is(DOMAIN_ID));
        assertThat(event.getProject(), is("/gdc/projects/" + PROJECT_ID));
        assertThat(event.getProcess(), is(PROCESS));
        assertThat(event.getExecution(), is(EXECUTION));
        assertThat(event.getInstance(), is(INSTANCE));
    }

}
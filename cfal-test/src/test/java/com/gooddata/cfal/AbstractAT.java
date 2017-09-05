/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.cfal;

import com.gooddata.CfalGoodData;
import com.gooddata.GoodDataEndpoint;
import com.gooddata.account.Account;
import com.gooddata.auditlog.AccountHelper;
import com.gooddata.auditlog.AdsHelper;
import com.gooddata.auditlog.AuditLogService;
import com.gooddata.auditlog.ProjectHelper;
import com.gooddata.auditlog.TestEnvironmentProperties;
import com.gooddata.cfal.restapi.dto.AuditEventDTO;
import com.gooddata.cfal.restapi.dto.RequestParameters;
import com.gooddata.collections.PageableList;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.testng.Assert.fail;

public abstract class AbstractAT {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final int POLL_LIMIT = 10;
    protected static final int POLL_INTERVAL_SECONDS = 30;

    protected final CfalGoodData gd;
    protected final GoodDataEndpoint endpoint;

    protected final AuditLogService service;

    protected final TestEnvironmentProperties props;

    private final DateTime startTime;

    protected final AdsHelper adsHelper;
    protected final AccountHelper accountHelper;
    protected final ProjectHelper projectHelper;

    public AbstractAT() {
        props = new TestEnvironmentProperties();

        endpoint = new GoodDataEndpoint(props.getHost());

        gd = new CfalGoodData(endpoint, props.getUser(), props.getPass());
        service = gd.getAuditLogService();

        startTime = new DateTime();

        this.adsHelper = AdsHelper.getInstance(gd, props);
        this.accountHelper = AccountHelper.getInstance(gd, props);
        this.projectHelper = ProjectHelper.getInstance(gd, props);
    }

    @BeforeSuite(alwaysRun = true)
    public void logConnectionInfo() throws Exception {
        logger.info("host={} user={} domain={}", props.getHost(), props.getUser(), props.getDomain());
    }

    @AfterSuite(alwaysRun = true)
    public void serviceTearDown() throws Exception {
        accountHelper.destroy();
        adsHelper.destroy();
        projectHelper.destroy();
    }

    /**
     * Tests whether message is contained in audit log via user API
     *
     * @param predicate predicate used to checker whether list of audit events contains required message
     * @param type               type of the even you want to check on API
     */
    public void doTestUserApi(final Predicate<AuditEventDTO> predicate, final String type) {
        final RequestParameters request = createRequestParameters(type);

        final PageableList<AuditEventDTO> events = service.listAuditEvents(getAccount(), request);

        doTest(events, predicate, type);
    }

    /**
     * Tests whether message is contained in audit log via admin API
     *
     * @param predicate predicate used to checker whether list of audit events contains required message
     * @param type               type of the even you want to check on API
     */
    public void doTestAdminApi(final Predicate<AuditEventDTO> predicate, final String type) {
        final RequestParameters request = createRequestParameters(type);

        final PageableList<AuditEventDTO> events = service.listAuditEvents(props.getDomain(), request);

        doTest(events, predicate, type);
    }

    private void doTest(final PageableList<AuditEventDTO> events,
                        final Predicate<AuditEventDTO> predicate,
                        final String type) {
        final String testMethodName = getTestMethodName();

        //poll until message is found in audit log or poll limit is hit
        int count = 1;
        while (count++ <= POLL_LIMIT) {
            if (hasMessage(events, predicate)) {
                logger.info("{}(): message {} found", testMethodName, type);
                return;
            }
            logger.info("{}(): message {} not found, waiting {} seconds", testMethodName, type, POLL_INTERVAL_SECONDS);
            try {
                TimeUnit.SECONDS.sleep(POLL_INTERVAL_SECONDS);
            } catch (InterruptedException e) {
                fail("Interrupted while waiting for message " + type, e);
            }
        }

        logger.error("{}(): message {} not found", testMethodName, type);
        fail("message " + type + " not found");
    }

    protected String getTestMethodName() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                    .filter(e -> Objects.equals(e.getClassName(), getClass().getName()))
                    .map(StackTraceElement::getMethodName)
                    .findFirst()
                    .orElse("unknown");
    }

    private boolean hasMessage(final PageableList<AuditEventDTO> events, final Predicate<AuditEventDTO> predicate) {
        return events.stream().anyMatch(predicate);
    }

    private RequestParameters createRequestParameters(final String type) {
        final RequestParameters requestParameters = new RequestParameters();
        requestParameters.setFrom(startTime);
        requestParameters.setType(type);
        return requestParameters;
    }

    public Account getAccount() {
        return accountHelper.getCurrentAccount();
    }
}

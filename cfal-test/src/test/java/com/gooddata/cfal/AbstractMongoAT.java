/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.cfal;

import com.gooddata.test.ssh.Authentication;
import com.gooddata.test.ssh.CommandResult;
import com.gooddata.test.ssh.SshClient;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.util.UriTemplate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.fail;

public abstract class AbstractMongoAT extends AbstractAT {

    private static final int MONGO_PORT = 27017;

    private MongoTemplate mongo;

    protected SshClient ssh;

    @BeforeClass
    public void setUpSshAndMongo() throws Exception {
        final Authentication auth = props.getSshAuth();
        ssh = new SshClient(endpoint.getHostname(), auth).open();

        final String mongoPass = obtainMongoPass(ssh);
        final int mongoPort = ssh.createLocalPortForwarder(MONGO_PORT);
        final String uri = new UriTemplate("mongodb://gdc_root:{pass}@localhost:{port}")
                .expand(mongoPass, mongoPort)
                .toString();
        mongo = new MongoTemplate(new MongoClient(new MongoClientURI(uri)), "cfal");
    }

    private String obtainMongoPass(final SshClient ssh) {
        final CommandResult result = ssh.execCmd("sudo cat /etc/gdc/etc/mongo");
        if (result.getExitCode() != 0) {
            throw new IllegalStateException("Unable to obtain mongo password: " + result.toString());
        }
        return result.getStdout();
    }

    @AfterClass
    public void tearDownSsh() throws Exception {
        if (ssh != null) {
            ssh.close();
        }
    }

    protected void assertQuery(final Query query, final String collectionName) throws Exception {
        final String testMethodName = getTestMethodName();
        int count = 0;
        while (!mongo.exists(query, collectionName)) {
            if (++count > POLL_LIMIT) {
                fail("Query in collection " + collectionName + " didn't return result: " + query);
            }
            logger.info("{}(): message in collection {} not found, waiting {} seconds",
                    testMethodName, collectionName, POLL_INTERVAL_SECONDS);
            TimeUnit.SECONDS.sleep(POLL_INTERVAL_SECONDS);
        }
        logger.info("{}(): message in collection {} found", testMethodName, collectionName);
    }}
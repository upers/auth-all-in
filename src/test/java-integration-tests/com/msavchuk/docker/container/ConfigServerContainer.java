package com.msavchuk.docker.container;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ConfigServerContainer implements TestRule {

    private static final Logger logger = LoggerFactory.getLogger(ConfigServerContainer.class);

    public ConfigServerContainer() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        super("iflavoursbv/mvn-openjdk-8-alpine:latest");
//        withFileSystemBind( "../", "/opt", BindMode.READ_ONLY);
//        withFileSystemBind( "/Users/mishasavchuk/.m2", "/root/.m2", BindMode.READ_WRITE);
//        withLogConsumer(new Slf4jLogConsumer(logger));

//        withCommand("mvn -f /opt/ConfigurationService/pom.xml install && cd /opt/ConfigurationService/target/release/ && sh start.sh& ");
//        withCommand("mvn", "-f", "/opt/ConfigurationService/pom.xml", "install", "&&", "cd", "/opt/ConfigurationService/target/release/", "&&", "sh", "./start.sh&");
//        withCommand("mvn", "-f", "/opt/ConfigurationService/pom.xml", "install");
//        with
//        withCommand("cd", "/opt/ConfigurationService/target/release/");
//        withCommand("sh", "./start.sh&");
//        waitUntilContainerStarted();
//        addFixedExposedPort(8888, 8888);

    }

    @Override
    public Statement apply(Statement statement, Description description) {
        return new Statement() {
            public void evaluate() throws Throwable {
                ArrayList errors = new ArrayList();

                try {
//                    ConfigServerContainer.this.starting(description);
                    statement.evaluate();
//                    ConfigServerContainer.this.succeeded(description);
                } catch (Throwable var6) {
                    errors.add(var6);
//                    ConfigServerContainer.this.failed(var6, description);
                } finally {
//                    ConfigServerContainer.this.finished(description);
                }

                MultipleFailureException.assertEmpty(errors);
            }
        };
    }

//    @Override
//    protected void containerIsStarted(InspectContainerResponse containerInfo) {
    //        client = new MockServerClient(getContainerIpAddress(), getMappedPort(80));
//    }
}
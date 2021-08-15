package com.msavchuk.test;

import com.msavchuk.AuthApplication;
import com.msavchuk.config.SpringTestConfig;
import com.msavchuk.config.constant.ApiVersion;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;

import javax.annotation.PostConstruct;
import java.io.File;
import java.time.Duration;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import({SpringTestConfig.class})
//@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
//@TestPropertySource(locations = "classpath:test.properties")
@Slf4j
public abstract class AbstractIntegrationTest {

    protected static DockerComposeContainer dockerContainers = new DockerComposeContainer(new File(
            "src/test/resources/docker-compose.yml"))
            .withLocalCompose(true)
            .withExposedService("postgres", 5432);

    static {
        dockerContainers.start();
    }

    protected volatile String apiEntryPoint;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void postConstructor() {
        String port = environment.getProperty("server.port");
        apiEntryPoint = "http://localhost:" + port + ApiVersion.API_PREFIX;
    }

    public static class MyHostPortWaitStrategy extends HostPortWaitStrategy {
        public MyHostPortWaitStrategy() {
            this.startupTimeout = Duration.ofSeconds(180L);
        }
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//            TestPropertyValues values = TestPropertyValues.of(
//                    "spring.redis.host=" + redis.getContainerIpAddress(),
//                    "spring.redis.port=" + redis.getMappedPort(6379)
//            );
//            values.applyTo(configurableApplicationContext);
        }
    }

}

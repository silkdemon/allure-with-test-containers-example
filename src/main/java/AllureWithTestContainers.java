import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

public class AllureWithTestContainers {

    Network network = Network.newNetwork();

    File file = new File("Dockerfile");

    public GenericContainer<?> allure =
//            new GenericContainer<>(new ImageFromDockerfile().withFileFromPath("Dockerfile", Paths.get("Dockerfile")))
            new GenericContainer<>(new ImageFromDockerfile()
                    .withFileFromPath(".", Paths.get("."))
                    .withFileFromPath("Dockerfile", Paths.get("./docker-custom/Dockerfile")))
                    .withNetwork(network)
                    .withExposedPorts(5050)
                    .waitingFor(new HttpWaitStrategy().forPort(5050)
                            .withStartupTimeout(Duration.ofMinutes(3)))
                    .withEnv("DEV_MODE", "0")
                    .withEnv("CHECK_RESULTS_EVERY_SECONDS", "NONR")
                    .withEnv("KEEP_HISTORY", "1")
                    .withEnv("KEEP_HISTORY_LATEST", "5")
                    .withEnv("SECURITY_USER", "my_username")
                    .withEnv("SECURITY_PASS", "my_password")
                    .withEnv("SECURITY_VIEWER_USER", "view_user")
                    .withEnv("SECURITY_VIEWER_PASS", "view_pass")
                    .withEnv("SECURITY_ENABLED", "1")
                    .withEnv("MAKE_VIEWER_ENDPOINTS_PUBLIC", "0")
                    .withEnv("OPTIMIZE_STORAGE", "0")
                    .withEnv("API_RESPONSE_LESS_VERBOSE", "0")
                    .withEnv("TLS", "0");


    @Before
    public void setUp() throws InterruptedException {
        allure.start();

        GenericContainer allureUI = new GenericContainer(DockerImageName.parse("frankescobar/allure-docker-service-ui"))
                .withNetwork(network)
                .dependsOn(allure)
                .withEnv("ALLURE_DOCKER_PUBLIC_API_URL", "http://localhost:" + allure.getMappedPort(5050))
                .withEnv("ALLURE_DOCKER_PUBLIC_API_URL_PREFIX", "")
                .withExposedPorts(5252);

        System.out.println("allure started, port: " + allure.getMappedPort(5050));
        allureUI.start();
        System.out.println("ui started, port: " + allureUI.getMappedPort(5252));
    }

    @Test
    public void test() throws InterruptedException {
        Thread.sleep(100000000);
    }

}

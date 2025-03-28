package integration;

import com.aau.wizard.WizardApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = WizardApplication.class)
class GameWebSocketIntegrationTest {
    // TODO: Write integration tests testing the communication via websockets
}
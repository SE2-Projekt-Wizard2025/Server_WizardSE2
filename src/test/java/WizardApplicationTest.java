import com.aau.wizard.WizardApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(classes = WizardApplication.class)
class WizardApplicationTest {

    @Test
    void contextLoads() {
        // If the context fails, the test will fail
    }

    @Test
    void mainMethodShouldStartApplicationWithoutExceptions() {
        assertDoesNotThrow(() -> WizardApplication.main(new String[]{}));
    }
}

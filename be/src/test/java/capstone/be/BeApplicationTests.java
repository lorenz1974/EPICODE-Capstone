package capstone.be;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class BeApplicationTests {

	@Test
	void contextLoads() {
	}

	public static void main(String[] args) {
		System.out.println("Hello TEST World!");
	}

	@Test
	void checkTestPropertiesExists() {
		InputStream is = getClass().getClassLoader().getResourceAsStream("application-test.properties");
		assertNotNull(is, "application-test.properties non trovato!");
	}

}

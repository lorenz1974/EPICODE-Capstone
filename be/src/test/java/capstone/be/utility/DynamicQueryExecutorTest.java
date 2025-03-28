package capstone.be.utility;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//
// Unit test for DynamicQueryExecutor class
//
@SpringBootTest
@Slf4j
public class DynamicQueryExecutorTest {

    @Autowired
    private DynamicQueryExecutor dynamicQueryExecutor;

    //
    // Tests if the query retrieves the correct user with ID 1 and username
    // "superadmin"
    //
    @Test
    public void testExecuteNativeQuery() {
        String sql = "SELECT * FROM users WHERE id = 1";
        List<Map<String, Object>> results = dynamicQueryExecutor.executeNativeQuery(sql);

        // Verify that we retrieved exactly one record
        assertFalse(results.isEmpty(), "No results found!");
        assertEquals(1, results.size(), "More than one record found!");

        // Verify the username field
        Map<String, Object> record = results.get(0);
        assertTrue(record.containsKey("username"), "Column 'username' not found!");
        assertEquals("superadmin", record.get("username"), "Username does not match expected value!");
    }
}

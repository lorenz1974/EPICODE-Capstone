package capstone.be.utility;

import com.fasterxml.jackson.databind.ObjectMapper;

import capstone.be.observer.EnvelopePollResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@JsonTest // Enables testing of JSON serialization/deserialization, disables JPA/Hibernate
public class EnvelopePollResponseTest {

  @Autowired
  private ObjectMapper objectMapper;

  //
  // Test for a successful API response
  @Test
  public void shouldDeserializeSuccessResponse() throws Exception {
    String jsonResponse = """
        {
          "Id": "d3bb33e2-36a4-43b1-9178-537e5c3fc875",
          "EnvelopeStatus": "Completed",
          "Name": "TEST API01 - Documento",
          "SentDate": "2025-03-11T09:25:11+00:00"
        }
        """;

    EnvelopePollResponse response = objectMapper.readValue(jsonResponse, EnvelopePollResponse.class);

    log.info("Deserialized Response: {}", response);

    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo("d3bb33e2-36a4-43b1-9178-537e5c3fc875");
    assertThat(response.getEnvelopeStatus()).isEqualTo("Completed");
    assertThat(response.getName()).isEqualTo("TEST API01 - Documento");
    assertThat(response.getSentDate()).isNotNull();
    assertThat(response.isError()).isFalse();
  }

  //
  // Test for an error API response
  @Test
  public void shouldDeserializeErrorResponse() throws Exception {
    String errorResponse = """
        {
          "ErrorId": "ERR0007",
          "Message": "ERR0007 - Envelope not available. - [EnvelopeId: d3bb33e2-36a4-43b1-9178-537e5c3fc876]",
          "TraceId": "5bfbc692-e3f4-4f69-b661-9a58859cd92e"
        }
        """;

    EnvelopePollResponse response = objectMapper.readValue(errorResponse, EnvelopePollResponse.class);

    log.info("Deserialized Error Response: {}", response);

    assertThat(response).isNotNull();
    assertThat(response.getErrorId()).isEqualTo("ERR0007");
    assertThat(response.getMessage())
        .isEqualTo("ERR0007 - Envelope not available. - [EnvelopeId: d3bb33e2-36a4-43b1-9178-537e5c3fc876]");
    assertThat(response.isError()).isTrue();
  }
}

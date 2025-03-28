package capstone.be.observer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import capstone.be.utility.LocalDateTimeDeserializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown fields from JSON
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from serialization
public class EnvelopePollResponse {

    // Fields for a successful response
    @JsonProperty("Id")
    private String id;

    @JsonProperty("EnvelopeStatus")
    private String envelopeStatus;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("SentDate")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime sentDate;

    // Fields for an error response
    @JsonProperty("ErrorId")
    private String errorId;

    @JsonProperty("Message")
    private String message;

    // Checks if the response is an error
    public boolean isError() {
        return errorId != null && message != null;
    }
}

package capstone.be.signrequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignRequestRequest {

    // Id is not strictly necessary, it is defined by the system
    private long signRequestId;

    //
    // Status is not modified by the user, it is modified by the system
    //
    // @NotNull(message = "status is required")
    // @Size(min = 4, max = 9, message = "status must be between 4 and 9
    // characters")
    // private SignRequestStatus status;

    @NotNull(message = "signedByAppUserId is required")
    @Min(value = 1, message = "signedByAppUserId must be greater than 0")
    private long signedByAppUserId;

    //
    // CreatedAt is not modified by the user, it is defined by the JWT token or is
    // changed only by the Administrators through the API
    // /signrequests/{id}/setcreatedby (post)
    //
    // For those reasons no validation is necessary
    // @NotNull(message = "createdByAppUserId is required")
    // @Min(value = 1, message = "createdByAppUserId must be greater than 0")
    private long createdByAppUserId;

    @NotNull(message = "docTemplateId is required")
    @Min(value = 1, message = "docTemplateId must be greater than 0")
    private long docTemplateId;
}

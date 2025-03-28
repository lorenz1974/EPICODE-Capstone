package capstone.be.signrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignRequestSimpleStats {
    private SignRequestStatus status;
    private Long count;
}

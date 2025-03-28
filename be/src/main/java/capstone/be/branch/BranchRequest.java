package capstone.be.branch;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchRequest {
    private Long id;
    private String description;
    private String alternativeDescription;
    private String city;
    private String province;
    private String cap;
    private String address;
    private String phone;
    private String piva;
    private LocalDateTime contributionStart;
    private String mail;
    private long company_Id;
}

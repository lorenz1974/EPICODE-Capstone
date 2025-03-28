package capstone.be.contract;

import lombok.Data;

@Data
public class ContractRequest {
    private Long id;
    private String level;
    private Long sector_Id;
}

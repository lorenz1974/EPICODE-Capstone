package capstone.be.company;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class CompanyRequest {
    private Long id;
    private String description;
    private String alternativeDescription;
    private String city;
    private String province;
    private String cap;
    private String address;
    private String phone;
    private String piva;
    private String notes;
    private String mail;
    private long sector_id;
}
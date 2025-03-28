package capstone.be.branch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import capstone.be.company.Company;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "branches")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company;
}

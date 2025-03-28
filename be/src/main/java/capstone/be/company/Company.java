package capstone.be.company;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import capstone.be.branch.Branch;
import capstone.be.sectors.Sector;

@Entity
@Data
@Table(name = "companies")
public class Company {
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
    private String notes;
    private String mail;

    @ManyToOne
    @JoinColumn(name = "sector_id")
    @JsonIgnoreProperties({ "companies", "contracts" })
    private Sector sector;

    @OneToMany(mappedBy = "company")
    private List<Branch> branches;
}

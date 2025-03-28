package capstone.be.sectors;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import capstone.be.company.Company;
import capstone.be.contract.Contract;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "sectors")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "companies", "contracts" })
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;

    @OneToMany(mappedBy = "sector")
    private List<Company> companies;

    @OneToMany(mappedBy = "sector")
    private List<Contract> contracts;

}

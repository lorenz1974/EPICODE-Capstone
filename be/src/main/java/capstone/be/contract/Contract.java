package capstone.be.contract;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import capstone.be.basesalary.BaseSalary;
import capstone.be.sectors.Sector;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contracts")
// @JsonIgnoreProperties({ "baseSalaries" })
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String level;

    @ManyToOne
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @OneToMany(mappedBy = "contract")
    @JsonIgnoreProperties({ "contract" })
    private List<BaseSalary> baseSalaries;

    @Override
    public String toString() {
        return "Contract [id=" + id + ", level=" + level + ", sectorId=" + sector.getId() + ", sectorName="
                + sector.getDescription() + "]";
    }
}

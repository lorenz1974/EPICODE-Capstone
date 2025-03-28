package capstone.be.basesalary;

import capstone.be.contract.Contract;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "base_salaries")
public class BaseSalary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double baseSalary;
    private Double contingency;
    private Double dueFullTime;
    private LocalDateTime valideFrom;
    private LocalDateTime valideTo;
    private Integer tredicesima;
    private Integer thirteenMonth;
    private Integer quattordicesima;
    private Integer fourteenMonth;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    @JsonIgnoreProperties({ "companies", "baseSalaries" })
    private Contract contract;
}

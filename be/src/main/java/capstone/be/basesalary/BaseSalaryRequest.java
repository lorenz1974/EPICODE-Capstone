package capstone.be.basesalary;

import lombok.Data;

@Data
public class BaseSalaryRequest {
    private Double baseSalary;
    private Double contingency;
    private Double dueFullTime;
    private String valideFrom;
    private String valideTo;
    private Integer tredicesima;
    private Integer thirteenMonth;
    private Integer quattordicesima;
    private Integer fourteenMonth;
    private Long contract_Id;
}

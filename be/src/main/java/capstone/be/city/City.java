package capstone.be.city;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cities")
public class City {
    @Id
    private long id;
    private String cityState;
    private String zipCode;
    private String cfCode;
    private int provinceId;
    private String province;
    private String region;
    private int stateId;
    private String state;
}

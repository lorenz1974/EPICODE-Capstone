package capstone.be.appuser;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.validation.annotation.Validated;

import capstone.be.jobprofile.JobProfileRequest;
import capstone.be.jobprofile.JobProfileType;
import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Validated
public class AppUserRegistrationRequest {

    protected String username;

    @NotNull(message = "Name cannot be blank")
    @Size(min = 2, message = "Name must be at least 2 characters")
    protected String name;

    @NotNull(message = "Surname cannot be blank")
    @Size(min = 2, message = "Surname must be at least 2 characters")
    protected String surname;

    @NotNull(message = "Sex cannot be blank")
    @Pattern(regexp = "^[FM]$", message = "Sex must be F or M")
    @Size(min = 1, message = "Sex must be at least 1 characters")
    protected String sex;

    @NotNull(message = "Fiscal code cannot be blank")
    @Size(min = 16, max = 16, message = "Fiscal code must be of 16 characters")
    protected String fiscalcode;

    @NotNull(message = "Birth date cannot be blank")
    @Past(message = "Birth date must be in the past")
    protected LocalDate birthDate;

    @NotNull(message = "Birth place cannot be blank")
    @Size(message = "Birth place must be at least 2 characters")
    protected String birthPlace;

    @NotNull(message = "Birth province cannot be blank")
    @Size(min = 2, message = "Birth province must be at least 2 characters")
    protected String birthProvince;

    @NotNull(message = "Nationality cannot be blank")
    @Size(min = 2, message = "Nationality must be at least 2 characters")
    protected String nationality;

    @NotNull(message = "Living city cannot be blank")
    @Size(min = 2, message = "Living city must be at least 2 characters")
    protected String livingCity;

    @NotNull(message = "Living province cannot be blank")
    @Size(min = 2, message = "Living province must be at least 2 characters")
    protected String livingProvince;

    @NotNull(message = "Address cannot be blank")
    @Size(min = 2, message = "Address must be at least 2 characters")
    protected String address;

    protected String addressco;

    @NotNull(message = "Zip code cannot be blank")
    @Size(min = 5, max = 5, message = "Zip code must be of 5 characters")
    protected String zipCode;

    @Column(nullable = true)
    protected String phone;

    @NotNull(message = "Cellphone cannot be blank")
    @Size(min = 10, message = "Cellphone must be at least 10 characters")
    protected String cellphone;

    @NotNull(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(min = 5, message = "Email must be at least 5 characters")
    protected String email;

    protected String notes;

    protected JobProfileRequest jobProfile;

    //
    // The service will retrive the fatherId from the token
    //
    // @Min(value = 0, message = "Father ID must be at least 0")
    // @Column(nullable = false)
    // protected Long fatherId;

    private String password;

}

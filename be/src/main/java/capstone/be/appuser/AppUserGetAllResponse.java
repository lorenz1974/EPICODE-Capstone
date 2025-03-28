package capstone.be.appuser;

import java.time.LocalDate;

import capstone.be.jobprofile.JobProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserGetAllResponse {

    private Long id;
    private String username;
    private String name;
    private String surname;
    private String fiscalcode;
    private String sex;
    private LocalDate birthDate;
    private String birthPlace;
    private String birthProvince;
    protected String livingCity;
    protected String livingProvince;
    protected String address;
    protected String zipCode;
    protected String phone;
    protected String cellphone;
    protected String email;
    protected String cap;
    protected String mail;
    protected String notes;

    private String nameSurname;
    private String surnameName;

    private JobProfile jobProfile;

    private boolean isDeleted = false;
}

package capstone.be.appuser;

import capstone.be.utility.LocalDateTimeDeserializer;
import jakarta.persistence.Column;
import capstone.be.utility.LocalDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Set;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppUserJSON {

    private Long id;
    private String username;
    private String name;
    private String surname;
    private String sex;
    private String fiscalcode;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String birthPlace;
    private String birthProvince;
    private String nationality;
    private String livingCity;
    private String livingProvince;
    private String address;
    private String addressco;
    private String zipCode;
    private String phone;
    private String cellphone;
    private String email;
    private String cap;
    private String mail;
    private String notes;
    private Long fatherId;
    private String nameSurname;
    private String surnameName;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    @Column(name = "password_updated_at", nullable = false)
    private LocalDateTime passwordUpdatedAt;

    private Set<AppUserRole> roles;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    // @JsonProperty("authorities")
    // @JsonDeserialize(using = GrantedAuthorityDeserializer.class)
    // private Set<GrantedAuthority> authorities;

    // public void setAuthorities(Set<GrantedAuthority> authorities) {
    // this.authorities = authorities.stream()
    // .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
    // .collect(Collectors.toSet());
    // }
}
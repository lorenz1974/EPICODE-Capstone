package capstone.be.appuser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Objects;

import capstone.be.doctemplate.DocTemplate;
import capstone.be.jobprofile.JobProfile;
import capstone.be.signrequest.SignRequest;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@JsonIgnoreProperties({ "authorities" })
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotNull(message = "Username cannot be blank")
    @Size(min = 2, message = "Username must be at least 2 characters")
    @Column(unique = true, nullable = false)
    protected String username;

    @NotNull(message = "Name cannot be blank")
    @Size(min = 2, message = "Name must be at least 2 characters")
    @Column(nullable = false)
    protected String name;

    @NotNull(message = "Surname cannot be blank")
    @Size(min = 2, message = "Surname must be at least 2 characters")
    @Column(nullable = false)
    protected String surname;

    @NotNull(message = "Sex cannot be blank")
    @Pattern(regexp = "^[FM]$", message = "Sex must be F or M")
    @Size(min = 1, message = "Sex must be at least 1 characters")
    @Column(nullable = false)
    protected String sex;

    @NotNull(message = "Fiscal code cannot be blank")
    @Size(min = 16, max = 16, message = "Fiscal code must be of 16 characters")
    @Column(nullable = false)
    protected String fiscalcode;

    @NotNull(message = "Birth date cannot be blank")
    @Past(message = "Birth date must be in the past")
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    protected LocalDate birthDate;

    @NotNull(message = "Birth place cannot be blank")
    @Size(message = "Birth place must be at least 2 characters")
    @Column(nullable = false)
    protected String birthPlace;

    @NotNull(message = "Birth province cannot be blank")
    @Size(min = 2, message = "Birth province must be at least 2 characters")
    @Column(nullable = false)
    protected String birthProvince;

    @NotNull(message = "Nationality cannot be blank")
    @Size(min = 2, message = "Nationality must be at least 2 characters")
    @Column(nullable = false)
    protected String nationality;

    @NotNull(message = "Living city cannot be blank")
    @Size(min = 2, message = "Living city must be at least 2 characters")
    @Column(nullable = false)
    protected String livingCity;

    @NotNull(message = "Living province cannot be blank")
    @Size(min = 2, message = "Living province must be at least 2 characters")
    @Column(nullable = false)
    protected String livingProvince;

    @NotNull(message = "Address cannot be blank")
    @Size(min = 2, message = "Address must be at least 2 characters")
    @Column(nullable = false)
    protected String address;

    @Column(nullable = true)
    protected String addressco;

    @NotNull(message = "Zip code cannot be blank")
    @Size(min = 5, max = 5, message = "Zip code must be of 5 characters")
    @Column(nullable = false, length = 5)
    protected String zipCode;

    @Column(nullable = true)
    protected String phone;

    @NotNull(message = "Cellphone cannot be blank")
    @Size(min = 10, message = "Cellphone must be at least 10 characters")
    @Column(nullable = false)
    protected String cellphone;

    @NotNull(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(min = 5, message = "Email must be at least 5 characters")
    @Column(nullable = false)
    protected String email;

    protected String cap;
    protected String mail;
    protected String notes;

    @Min(value = 0, message = "Father ID must be at least 0")
    @Column(nullable = false)
    protected Long fatherId;

    // It coulb be null because the service sets it to a random password
    // @NotNull(message = "Password cannot be blank")
    // @Size(min = 2, message = "Password must be at least 2 characters")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    private String nameSurname;
    private String surnameName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "password_updated_at", nullable = false)
    private LocalDateTime passwordUpdatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<AppUserRole> roles;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE })
    @JoinTable(name = "app_user_allowed_doc_template", joinColumns = @JoinColumn(name = "app_user_id"), inverseJoinColumns = @JoinColumn(name = "doc_template_id"))
    @JsonIncludeProperties({ "id", "name" })
    private List<DocTemplate> docTemplatesAllowed;

    //
    // The requests the user has created as Secretary or Operator
    //
    @OneToMany(mappedBy = "createdByAppUser", orphanRemoval = false, fetch = FetchType.EAGER)
    @JsonIncludeProperties({ "id", "subject", "docTemplate", "status", "createdAt", "updatedAt" })
    private List<SignRequest> signRequestsCreated;

    //
    // The requests the user has signed as Signer
    //
    @OneToMany(mappedBy = "signedByAppUser", orphanRemoval = false, fetch = FetchType.EAGER)
    @JsonIncludeProperties({ "id", "subject", "docTemplate", "status", "createdAt", "updatedAt" })
    private List<SignRequest> signRequestsSigned;

    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JsonIgnoreProperties({ "appUser" })
    private JobProfile jobProfile;

    // --- METODI NON STANDARD LOMBOK
    // -------------------------------------------------------------
    @PreRemove
    private void preRemove() {
        this.roles.clear();
        this.docTemplatesAllowed.clear();
        this.signRequestsCreated.clear();
        this.signRequestsSigned.clear();
        this.jobProfile = null;
    }

    public void setName(String name) {
        this.name = name;
        this.nameSurname = this.name + " " + this.surname;
        this.surnameName = this.surname + " " + this.name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
        this.nameSurname = this.name + " " + this.surname;
        this.surnameName = this.surname + " " + this.name;
    }

    // Set fiscalCode to upperCase()
    public void setFiscalcode(String fiscalcode) {
        this.fiscalcode = fiscalcode.toUpperCase();
    }

    // Creo il setPassword perché questo mi permette di aggiornare la data di cambio
    public void setPassword(String password) {
        // Per ragioni di standard la password deve arrivare già criptata
        this.password = password;
        this.passwordUpdatedAt = LocalDateTime.now();
    }

    // Override methods to ensure username and email are always lowercase
    public void setUsername(String username) {
        this.username = username.toLowerCase().replace(" ", "").replace("'", "");
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
        // setto anche mail per evitare problemi di compatibilità con le altre classi
        // che usano 'mail'
        this.mail = email.toLowerCase();
    }

    public void setMail(String mail) {
        this.mail = mail.toLowerCase();
        // setto anche mail per evitare problemi di compatibilità con le altre classi
        // che usano 'mail'
        this.email = mail.toLowerCase();
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode.toUpperCase();
        this.cap = zipCode.toUpperCase();
    }

    public void setCap(String cap) {
        this.cap = cap.toUpperCase();
        this.zipCode = cap.toUpperCase();
    }

    public void eraseCredentials() {
        this.password = "**********";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> (GrantedAuthority) role::name)
                .collect(Collectors.toSet());
    }

    public void addDocTemplate(DocTemplate docTemplate) {
        docTemplatesAllowed.add(docTemplate);
        docTemplate.getAppUsers().add(this);
    }

    public void removeDocTemplate(DocTemplate docTemplate) {
        docTemplatesAllowed.remove(docTemplate);
        docTemplate.getAppUsers().remove(this);
    }

    @Override
    public int hashCode() {
        // Ensure no recursive calls or infinite loops
        return Objects.hash(id, username, email); // Use relevant fields
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", fiscalcode='" + fiscalcode + '\'' +
                ", birthDate=" + birthDate +
                ", birthPlace='" + birthPlace + '\'' +
                ", birthProvince='" + birthProvince + '\'' +
                ", company=" + jobProfile.getCompany().getDescription() +
                ", branch=" + jobProfile.getBranch().getDescription() +
                ", contract=" + jobProfile.getContract().getLevel() +
                '}';
    }
}

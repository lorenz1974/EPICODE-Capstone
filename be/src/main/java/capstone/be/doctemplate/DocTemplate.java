package capstone.be.doctemplate;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import capstone.be.appuser.AppUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "doc_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true)
    private String subject;

    @Column(nullable = true, length = 500)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String templateJson; // JSON data

    // The class that will be used to parse the data of the template. Not strigly
    // (e.g. "capstone.be.utility.DynamicJsonParser")
    // neccessary because of the use of the DynamicJsonParser.
    @Column(nullable = true)
    private String dataTemplateClass;

    @Column(nullable = true)
    private String templatePath;

    @Column(nullable = true)
    private String templateProviderFileApiEndpoint;

    @Column(nullable = true)
    private String templateProviderFileIdField;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String templateSqlQuery;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String templateProvider;

    @Column(nullable = false)
    private String templateProviderApiEndpoint;

    // Method to send the request (typically POST)
    @Column(nullable = false)
    private String templateApiCrudType;

    // Response value to retrive the id of the created object
    @Column(nullable = false)
    private String templateProviderApiEndpointIdField;

    // Url where check the status of request
    @Column(nullable = false)
    private String templateApiCrudCheckEndpoint;

    // Method to check the status of request (GET, POST, typically GET)
    @Column(nullable = false)
    private String templateApiCrudCheckType;

    // The name of the field of the api response that contains the status of the
    // request
    // (e.g. "status", "state", "completed", "status_code")
    @Column(nullable = false)
    private String templateApiCrudCheckStatusField;

    // The values that indicates that the request is completed
    @Column(nullable = false)
    private Set<String> templateApiCrudCheckCompletedValues;

    // The values that indicates that the request is failed
    @Column(nullable = false)
    private Set<String> templateApiCrudCheckFailedValues;

    // The values that indicates that the request is waiting for some action
    // Tipically when the provider is waiting for the user to sign the document
    // (e.g. "waiting_for_signature", "pending", "active")
    @Column(nullable = false)
    private Set<String> templateApiCrudCheckWaitingValues;

    // The field of the api response that contains the error code
    @Column(nullable = false)
    private String templateApiCrudErrorField;

    // The field of the api response that contains the error message
    @Column(nullable = true)
    private String templateApiCrudErrorMessageField;

    // The values that indicates that the request is failed
    @Column(nullable = false)
    private Set<String> templateApiCrudErrorValues;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @ManyToMany(mappedBy = "docTemplatesAllowed", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<AppUser> appUsers;

    public void removeAppUser(AppUser appUser) {
        appUsers.remove(appUser);
        appUser.getDocTemplatesAllowed().remove(this);
    }

    public void addAppUser(AppUser AppUser) {
        appUsers.add(AppUser);
        AppUser.getDocTemplatesAllowed().add(this);
    }

    @Override
    public String toString() {
        return "DocTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subject='" + subject + '\'' +
                ", templateProvider='" + templateProvider + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
package capstone.be.signrequest;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import capstone.be.appuser.AppUser;
import capstone.be.doctemplate.DocTemplate;
import capstone.be.signrequestchronology.SignRequestChronology;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sign_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "signedByAppUser", "createdByAppUser", "docTemplate", "chronology" })
public class SignRequest {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column(nullable = true)
        private String subject;

        @Column(nullable = true)
        private String documentPath;

        // unique for each provider. It will be added later when released by the
        // provider
        @Column(nullable = true, unique = true)
        private String providerEnvelopeId;

        @Enumerated(EnumType.STRING)
        private SignRequestStatus status;

        @CreationTimestamp
        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at", nullable = false)
        private LocalDateTime updatedAt;

        @Column(nullable = false)
        private boolean isDeleted = false;

        @ManyToOne
        @JoinColumn(name = "signed_by_app_user_id", nullable = true)
        @OnDelete(action = OnDeleteAction.NO_ACTION)
        @JsonIncludeProperties(value = { "id", "name", "surname" })
        private AppUser signedByAppUser;

        @ManyToOne
        @JoinColumn(name = "created_by_app_user_id", nullable = false)
        @OnDelete(action = OnDeleteAction.NO_ACTION)
        @JsonIncludeProperties(value = { "id", "name", "surname" })
        private AppUser createdByAppUser;

        @ManyToOne
        @JoinColumn(name = "doc_template_id", nullable = false)
        @JsonIncludeProperties(value = { "id", "name" })
        private DocTemplate docTemplate;

        @OneToMany(mappedBy = "signRequest", cascade = { CascadeType.MERGE,
                        CascadeType.PERSIST }, orphanRemoval = true, fetch = FetchType.EAGER)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JsonIgnore
        private List<SignRequestChronology> chronology;

}

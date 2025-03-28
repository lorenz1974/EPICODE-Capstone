package capstone.be.signrequestchronology;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import capstone.be.signrequest.SignRequest;

import java.time.LocalDateTime;

@Entity
@Table(name = "sign_requests_chronology")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignRequestChronology {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChronologyType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChronologyStatus status;

    @Column(length = 500)
    private String message;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String eventData; // JSON data

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "sign_request_id", nullable = false)
    @JsonIncludeProperties(value = { "id", "createdAt", "status" })
    private SignRequest signRequest;

}

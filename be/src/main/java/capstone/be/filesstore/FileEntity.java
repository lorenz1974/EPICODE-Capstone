package capstone.be.filesstore;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "files")
public class FileEntity {

    // Could not be @Generated because it's part of the business logic
    @Id
    private UUID id;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = true)
    private String fatherType;

    @Column(nullable = true)
    private Long fatherId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    public String getUuidFileName() {
        return id.toString() + (extension.isEmpty() ? "" : "." + extension);
    }

    public String getFullFilePath() {
        return storagePath + "/" + getUuidFileName();
    }
}

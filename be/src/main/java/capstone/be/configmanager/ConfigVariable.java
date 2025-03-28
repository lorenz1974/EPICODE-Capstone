package capstone.be.configmanager;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "config_variables")
@Data
@NoArgsConstructor
public class ConfigVariable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String variableName;

    @Column(nullable = false, columnDefinition = "TEXT") // Supporta JSON di grandi dimensioni
    private String value;

    @Column(nullable = false)
    private String type;

    @Column(nullable = true)
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ConfigVariable(String variableName, String value, String type) {
        this.variableName = variableName;
        this.value = value;
        this.type = type;
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

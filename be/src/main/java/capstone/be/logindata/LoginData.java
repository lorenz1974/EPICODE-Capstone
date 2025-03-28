package capstone.be.logindata;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "logins_data")
@Slf4j
public class LoginData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String username;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String ipAddress;
    @Column(nullable = false)
    private String userAgent;
    @Column(nullable = false)
    private String device;
    @Column(nullable = false)
    private String os;
    @Column(nullable = false)
    private String browser;
    @Column(nullable = false)
    private String status;
    @Column(nullable = true) // nullable = true perché non è obbligatorio
    private String message;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private String action;
    @Column(nullable = false)
    private String requestUrl;
    @Column(nullable = false)
    private String instance;
}

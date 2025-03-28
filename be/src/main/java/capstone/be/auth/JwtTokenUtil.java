package capstone.be.auth;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for handling JWT tokens.
 *
 * This class provides methods for generating, parsing, and validating JWT
 * tokens
 * within a Spring Security context.
 *
 * **Features:**
 * - Generates a JWT token containing user information and roles.
 * - Extracts claims (such as username and roles) from a token.
 * - Validates whether a token is expired or corresponds to a given user.
 *
 * **Security Considerations:**
 * - The `secretKey` should be long enough (at least 256 bits) for HMAC-SHA256.
 * - Tokens should have an appropriate expiration time to prevent misuse.
 * - The secret key should never be hardcoded in the source code but stored
 * securely.
 */
@Component
@Configuration
@RequiredArgsConstructor
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
@Slf4j
public class JwtTokenUtil {

    private String secretKey; // Secret key used for signing the JWT.
    private long expirationTime; // Expiration time in milliseconds.

    /**
     * Extracts the username (subject) from the given JWT token.
     *
     * @param token The JWT token.
     * @return The username extracted from the token.
     *
     *         **Why this approach?**
     *         - The `Claims::getSubject` method directly retrieves the subject
     *         field from the token.
     *         - Using a generic method (`getClaimFromToken()`) ensures reusability.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the given JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date of the token.
     *
     *         **Why extract expiration separately?**
     *         - This is useful for checking token validity before accessing
     *         protected resources.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Generic method to retrieve a specific claim from a JWT token.
     *
     * @param <T>            The type of the claim to be returned.
     * @param token          The JWT token.
     * @param claimsResolver A function to extract the desired claim.
     * @return The extracted claim of type T.
     *
     *         **Why use generics?**
     *         - This provides flexibility, allowing us to extract different claim
     *         types using a single method.
     *         - Helps avoid repetitive code for each claim type.
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the JWT token and retrieves all claims.
     *
     * @param token The JWT token.
     * @return The claims extracted from the token.
     *
     *         **Why use `parserBuilder()` instead of `parser()`?**
     *         - `parserBuilder()` is recommended for security reasons and supports
     *         enhanced key handling.
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())) // Ensures the key is properly handled.
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks whether a JWT token has expired.
     *
     * @param token The JWT token.
     * @return `true` if the token is expired, `false` otherwise.
     *
     *         **Why compare against `new Date()`?**
     *         - This ensures the check is always performed against the current
     *         server time.
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Generates a JWT token for a given user, including their roles.
     *
     * @param userDetails The user information.
     * @return A signed JWT token.
     *
     *         **Token structure:**
     *         - Subject: The username of the user.
     *         - Claim "roles": A list of roles associated with the user.
     *         - Issued at: The time when the token was generated.
     *         - Expiration: The time when the token will expire.
     *         - Signature: The token is signed using HMAC-SHA256.
     *
     *         **Why include roles in the token?**
     *         - This allows role-based authorization without requiring a database
     *         lookup.
     *         - However, this approach may lead to security risks if roles change
     *         frequently.
     */
    public String generateToken(UserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Debugging logs (can be removed in production).
        log.debug("secretKey: " + secretKey);
        log.debug("expirationTime: " + expirationTime);
        log.debug("User roles: " + roles);

        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Sets the username as the token subject.
                .claim("roles", roles) // Adds roles to the token.
                .setIssuedAt(new Date(System.currentTimeMillis())) // Token issue time.
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Expiration time.
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256) // Signs the token.
                .compact();
    }

    /**
     * Extracts roles from a JWT token.
     *
     * @param token The JWT token.
     * @return A set of role names extracted from the token.
     *
     *         **Why use a Set instead of a List?**
     *         - Roles are unique by nature, and using a Set prevents duplicates.
     */
    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (Set<String>) claims.get("roles", Set.class);
    }

    /**
     * Validates a JWT token against a given user's details.
     *
     * @param token       The JWT token.
     * @param userDetails The user details to validate against.
     * @return `true` if the token is valid and belongs to the user, `false`
     *         otherwise.
     *
     *         **Validation logic:**
     *         - Checks that the username extracted from the token matches the
     *         expected username.
     *         - Ensures that the token is not expired.
     *
     *         **Why check both username and expiration?**
     *         - Checking only expiration would allow tokens from different users to
     *         be used interchangeably.
     *         - Ensuring the username matches prevents token spoofing.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

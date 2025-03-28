package capstone.be.auth;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import capstone.be.appuser.AppUserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final AppUserRepository appUserRepository;

    //
    // Retrieves the Authentication object of the current user.
    //
    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    //
    // Returns the username of the authenticated user.
    //
    public String getUsername() {
        Authentication authentication = getAuthentication();
        return (authentication != null) ? authentication.getName() : null;
    }

    //
    // Returns the user id
    //
    public Long getUserId() {
        String username = getUsername();
        return (username != null) ? appUserRepository.findByUsernameIgnoreCase(username).get().getId() : null;
    }

    //
    // Returns the roles of the authenticated user as a Set<String>.
    //
    public Set<String> getRolesAsSet() {
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
        }
        return Set.of(); // Returns an empty set if not authenticated
    }

    //
    // Returns the roles of the authenticated user as a concatenated string
    // separated by ", ".
    //
    public String getRolesAsString() {
        return String.join(", ", getRolesAsSet());
    }

    //
    // Returns the principal of the authenticated user.
    //
    public Object getPrincipal() {
        Authentication authentication = getAuthentication();
        return (authentication != null) ? authentication.getPrincipal() : null;
    }

    //
    // Returns the credentials of the authenticated user.
    //
    public Object getCredentials() {
        Authentication authentication = getAuthentication();
        return (authentication != null) ? authentication.getCredentials() : null;
    }

    //
    // Returns the details of the authenticated user.
    //
    public Object getDetails() {
        Authentication authentication = getAuthentication();
        return (authentication != null) ? authentication.getDetails() : null;
    }

    //
    // Checks whether the user is authenticated.
    //
    public boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    //
    // Invalidates the user's authentication.
    //
    public void invalidate() {
        SecurityContextHolder.clearContext();
    }

    //
    // Checks if the user has a specific role.
    //
    private boolean hasRole(String role) {
        return getRolesAsSet().contains(role);
    }

    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    public boolean isUser() {
        return hasRole("ROLE_USER");
    }

    public boolean isOperator() {
        return hasRole("ROLE_OPERATOR");
    }

    public boolean isSecretary() {
        return hasRole("ROLE_SECRETARY");
    }
}

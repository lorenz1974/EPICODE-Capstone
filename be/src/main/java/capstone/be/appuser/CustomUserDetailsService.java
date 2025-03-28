package capstone.be.appuser;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!appUserRepository.existsByUsernameIgnoreCaseOrEmailIgnoreCase(username, username)) {
            throw new UsernameNotFoundException("User '" + username + "' not found or wrong password");
        }
        return appUserRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(username, username).get();
    }
}

package capstone.be.logindata;

import capstone.be.utility.BrowserInfo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginDataService {

    private final HttpServletRequest request;
    private final LoginDataRepository loginDataRepository;
    private final BrowserInfo browserInfo;

    public Page<LoginData> getAllLoginData(Pageable pageable, String query, boolean alsoDeleted) {
        log.debug("Fetching all login data with query '{}' and alsoDeleted: {}", query, alsoDeleted);
        if (query == null || query.isEmpty()) {
            return loginDataRepository.findAll(pageable);
        }
        return loginDataRepository.omniSearch(query.toLowerCase(), pageable);
    }

    public void saveLoginData(LoginData loginData) {
        loginDataRepository.save(loginData);
    }

    public Page<LoginData> findByUsername(Pageable pageable, String username) {
        return loginDataRepository.findByUsername(username, pageable);
    }

    public void pushLoginData(String status, String username, String token, String message,
            String action) {

        LoginData loginData = new LoginData();
        loginData.setUsername(username != null ? username : "Unknown");
        loginData.setIpAddress(browserInfo.getClientIpAddress());
        loginData.setUserAgent(browserInfo.getUserAgent());
        loginData.setDevice(browserInfo.extractDevice());
        loginData.setOs(browserInfo.extractOS());
        loginData.setBrowser(browserInfo.extractBrowser());
        if (token == null || token.isEmpty()) {
            loginData.setToken(browserInfo.getToken());
        } else {
            loginData.setToken(token);
        }
        loginData.setRequestUrl(request.getRequestURL().toString());
        loginData.setInstance(request.getRequestURI());
        loginData.setStatus(status != null ? status : "Unknown");
        loginData.setAction(action != null ? action : "Unknown");
        loginData.setMessage(message != null ? message : "Unknown");

        log.debug("Recording LoginData: {}", loginData);

        saveLoginData(loginData);
    }

}

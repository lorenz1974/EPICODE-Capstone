package capstone.be.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BrowserInfo {

    private static final String UNKNOWN = "Unknown";

    private String userAgent; // Variabile di istanza per memorizzare lo User-Agent

    private HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    private void initializeUserAgent() {
        HttpServletRequest request = getCurrentRequest();
        this.userAgent = request.getHeader("User-Agent");
    }

    public String getToken() {
        HttpServletRequest request = getCurrentRequest();
        String authHeader = request.getHeader("Authorization");
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : UNKNOWN;
        log.debug("Extracted Auth Token: {}", token);
        return token;
    }

    public String getClientIpAddress() {
        HttpServletRequest request = getCurrentRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        log.debug("Extracted IP Address: {}", ip);
        return ip;
    }

    public String getUserAgent() {
        if (this.userAgent == null) {
            initializeUserAgent(); // Inizializza lo User-Agent se non è già stato fatto
        }
        log.debug("Extracted User-Agent: {}", this.userAgent);
        return this.userAgent != null ? this.userAgent : UNKNOWN;
    }

    public String extractDevice() {
        String userAgent = getUserAgent(); // Usa la variabile di istanza
        if (userAgent == null) {
            return UNKNOWN;
        }

        if (userAgent.contains("Mobile")) {
            return "Mobile";
        } else if (userAgent.contains("Tablet")) {
            return "Tablet";
        } else if (userAgent.contains("Windows") || userAgent.contains("Macintosh") || userAgent.contains("Linux")) {
            return "Desktop";
        } else {
            return UNKNOWN;
        }
    }

    public String extractOS() {
        String userAgent = getUserAgent(); // Usa la variabile di istanza
        if (userAgent == null) {
            return UNKNOWN;
        }

        if (userAgent.contains("Windows NT 10.0")) {
            return "Windows 10";
        } else if (userAgent.contains("Windows NT 6.3")) {
            return "Windows 8.1";
        } else if (userAgent.contains("Windows NT 6.2")) {
            return "Windows 8";
        } else if (userAgent.contains("Windows NT 6.1")) {
            return "Windows 7";
        } else if (userAgent.contains("Mac OS X")) {
            return "Mac OS";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone")) {
            return "iOS";
        } else {
            return UNKNOWN;
        }
    }

    public String extractBrowser() {
        String userAgent = getUserAgent(); // Usa la variabile di istanza
        if (userAgent == null) {
            return UNKNOWN;
        }

        Pattern pattern = Pattern.compile("(Chrome|Firefox|Safari|Edg|Opera|MSIE|Trident)[/ ]([\\d.]+)");
        Matcher matcher = pattern.matcher(userAgent);
        if (matcher.find()) {
            String browser = matcher.group(1);
            switch (browser) {
                case "MSIE":
                case "Trident":
                    return "Internet Explorer";
                case "Edg":
                    return "Edge";
                default:
                    return browser;
            }
        }
        return UNKNOWN;
    }
}
package capstone.be.auth;

import capstone.be.exception.ExceptionMessage;
import capstone.be.logindata.LoginDataService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Filtro JWT per intercettare e validare i token nelle richieste HTTP.
 * <p>
 * Questa classe estende {@link OncePerRequestFilter}, il che significa che il
 * filtro
 * viene eseguito una volta per ogni richiesta HTTP. Il suo scopo è:
 * - Estrarre il token JWT dall'header "Authorization".
 * - Validare il token e verificarne l'integrità.
 * - Recuperare il nome utente dal token e verificare se l'utente è già
 * autenticato.
 * - Se l'utente non è autenticato e il token è valido, impostare manualmente il
 * contesto di autenticazione di Spring Security.
 * <p>
 * Il token JWT deve essere passato nel formato:
 * ```
 * Authorization: Bearer <token>
 * ```
 * <p>
 * **Motivazioni della scelta:**
 * - `OncePerRequestFilter` è preferito rispetto ad altri filtri perché
 * garantisce che il filtro venga eseguito solo una volta per richiesta.
 * - Non viene effettuata alcuna gestione della sessione poiché JWT è stateless.
 * - Il filtro viene eseguito prima del
 * `UsernamePasswordAuthenticationFilter` per intercettare e autenticare gli
 * utenti prima che Spring Security verifichi le credenziali.
 * <p>
 * Alternative:
 * - Si potrebbe usare `BasicAuthenticationFilter` se si volesse gestire
 * un'autenticazione basata su sessioni.
 * - Se il token JWT venisse trasmesso come cookie anziché nell'header, si
 * dovrebbe estrarlo da `request.getCookies()`.
 *
 * @see OncePerRequestFilter
 * @see UsernamePasswordAuthenticationToken
 * @see SecurityContextHolder
 */
@Component
@RequiredArgsConstructor
@Slf4j

public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final LoginDataService loginDataService;

    /**
     * Intercetta le richieste HTTP per estrarre e validare il token JWT.
     *
     * @param request  La richiesta HTTP in ingresso.
     * @param response La risposta HTTP in uscita.
     * @param chain    La catena di filtri.
     * @throws ServletException In caso di errore durante il filtraggio.
     * @throws IOException      Se si verifica un errore di I/O.
     *                          <p>
     *                          Logica di elaborazione:
     *                          1. Legge l'header "Authorization" dalla richiesta
     *                          HTTP.
     *                          2. Se l'header è presente e inizia con "Bearer ",
     *                          estrae il token JWT.
     *                          3. Tenta di ottenere il nome utente dal token.
     *                          4. Se il token è valido e l'utente non è già
     *                          autenticato, carica i dettagli dell'utente e imposta
     *                          il contesto di sicurezza.
     *                          5. Prosegue la catena di filtri.
     *                          <p>
     *                          Alternative:
     *                          - Se il token non fosse nel formato "Bearer ", si
     *                          potrebbe accettare anche un formato JSON nel corpo
     *                          della richiesta.
     *                          - Se l'API dovesse supportare più metodi di
     *                          autenticazione, si potrebbe implementare un sistema
     *                          di fallback.
     */
    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);

            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);

            } catch (IllegalArgumentException e) {
                handleTokenException(response, "JWTERR001: Unable to get username from JWT Token", jwtToken);
                return;

            } catch (ExpiredJwtException e) {
                handleTokenException(response, "JWTERR002: JWT Token has expired", jwtToken);
                return;

            } catch (SignatureException e) {
                handleTokenException(response,
                        "JWTERR003: JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted",
                        jwtToken);
                return;

            } catch (SecurityException e) {
                handleTokenException(response, "JWTERR004: JWT Token security validation failed", jwtToken);
                return;

            } catch (MalformedJwtException e) {
                handleTokenException(response, "JWTERR005: JWT Token is malformed", jwtToken);
                return;

            } catch (UnsupportedJwtException e) {
                handleTokenException(response, "JWTERR006: JWT Token is unsupported", jwtToken);
                return;

            } catch (PrematureJwtException e) {
                handleTokenException(response, "JWTERR007: JWT Token is not yet valid", jwtToken);
                return;
            }
        } else {
            chain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                String message = "JWT authentication successful for user: " + username;

                log.debug(message);
                loginDataService.pushLoginData("SUCCESS", username, jwtToken, message, "TOKEN VALIDATION");

            } else {

                handleTokenException(response, "JWTERR008: Token JWT not valid for the user: " + username, jwtToken);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    //
    // This method is used to handle exceptions related to JWT token validation.
    // That's because the filter is not able to throw exceptions that can be managed
    // by the Global Exception Handler. (!!!)
    //
    private void handleTokenException(HttpServletResponse response, String message, String jwtToken)
            throws IOException {

        // Log the message and push the login data
        log.error(message);
        loginDataService.pushLoginData("FAIL", null, jwtToken, message, "TOKEN VALIDATION");

        // Create the error message
        ExceptionMessage exceptionMessage = new ExceptionMessage();

        exceptionMessage.setMessage(message);
        exceptionMessage.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        exceptionMessage.setError("Bad Request");
        exceptionMessage.setTimestamp(LocalDateTime.now());

        // Prepare the ObjectMapper and register the JavaTimeModule
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Prepare the response and send it
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(exceptionMessage));
    }
}

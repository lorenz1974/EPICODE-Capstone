package capstone.be.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Implementazione personalizzata di {@link AuthenticationEntryPoint} per
 * gestire tentativi di accesso non autorizzati.
 *
 * Questa classe viene utilizzata da Spring Security per intercettare richieste
 * non autorizzate
 * a risorse protette e restituire una risposta con codice di stato HTTP 401
 * (Unauthorized).
 *
 * Funzionamento:
 * - Se un utente tenta di accedere a un endpoint protetto senza un token JWT
 * valido, il metodo `commence()` viene invocato.
 * - Il metodo invia una risposta HTTP con codice 401 e un messaggio
 * "Unauthorized access".
 * - Non viene effettuato alcun reindirizzamento a una pagina di login, poiché
 * JWT utilizza un meccanismo stateless.
 *
 * Motivazioni della scelta:
 * - In una configurazione JWT stateless, non esistono sessioni o redirect
 * automatici alla pagina di login.
 * - Il codice 401 indica correttamente che l'autenticazione è richiesta.
 * - Evita di esporre dettagli dell'errore per motivi di sicurezza.
 *
 * Alternative:
 * - Si potrebbe restituire una risposta JSON dettagliata con un messaggio più
 * esplicativo, utile per client frontend.
 * - Se l'app usasse sessioni, si potrebbe reindirizzare l'utente a una pagina
 * di login invece di restituire il codice 401.
 * - Si potrebbe loggare l'errore per il monitoraggio degli accessi non
 * autorizzati.
 *
 * @see org.springframework.security.web.AuthenticationEntryPoint
 * @see jakarta.servlet.http.HttpServletRequest
 * @see jakarta.servlet.http.HttpServletResponse
 * @see org.springframework.security.core.AuthenticationException
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Gestisce le richieste non autorizzate restituendo un errore HTTP 401.
     *
     * @param request       L'oggetto {@link HttpServletRequest} che rappresenta la
     *                      richiesta HTTP ricevuta.
     * @param response      L'oggetto {@link HttpServletResponse} che rappresenta la
     *                      risposta HTTP da inviare.
     * @param authException L'eccezione {@link AuthenticationException} generata
     *                      quando un utente non autenticato tenta di accedere a una
     *                      risorsa protetta.
     * @throws IOException Se si verifica un errore nell'invio della risposta HTTP.
     *
     *                     Alternative:
     *                     - Se si desidera restituire una risposta JSON, si può
     *                     scrivere nel body della `response` invece di usare
     *                     `sendError()`.
     *                     - Si può loggare l'evento di accesso non autorizzato per
     *                     analizzare tentativi sospetti di accesso.
     */
    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
    }
}

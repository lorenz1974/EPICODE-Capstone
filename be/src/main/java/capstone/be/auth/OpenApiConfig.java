package capstone.be.auth;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura l'integrazione di OpenAPI (Swagger) con uno schema di sicurezza
 * basato su JWT.
 *
 * Questa configurazione consente a Swagger UI di supportare l'autenticazione
 * con token JWT,
 * in modo che le richieste ai servizi protetti possano includere
 * automaticamente il token
 * nell'header HTTP `Authorization`.
 *
 * Alternativa:
 * - OpenAPI consente anche altri metodi di autenticazione, come `API Key` o
 * `OAuth2`.
 * - Tuttavia, nel contesto di JWT, il metodo "bearer token" è il più indicato
 * per la gestione
 * dell'autenticazione stateless, evitando la necessità di sessioni o cookie.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Crea e configura un'istanza di OpenAPI con schema di sicurezza JWT (Bearer
     * Token).
     *
     * @return un'istanza di OpenAPI configurata con lo schema di sicurezza basato
     *         su JWT.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Definisce il nome dello schema di sicurezza, che sarà poi referenziato negli
        // endpoint protetti.
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Associa lo schema di sicurezza alle richieste API, rendendolo obbligatorio
                // per gli endpoint protetti.
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))

                // Configura i componenti di OpenAPI, includendo lo schema di autenticazione
                // JWT.
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                /**
                                 * Definisce il tipo di schema di sicurezza.
                                 * - `Type.HTTP` indica che il meccanismo di autenticazione avviene tramite un
                                 * header HTTP standard.
                                 * - Alternativa: `Type.APIKEY` potrebbe essere usato se volessimo un parametro
                                 * di autenticazione passato via query string o header personalizzato.
                                 */
                                .type(SecurityScheme.Type.HTTP)

                                /**
                                 * Specifica lo schema come "bearer", che è lo standard per i token JWT.
                                 * - Alternativa: Per altre autenticazioni HTTP, si potrebbe usare `basic`
                                 * (Basic Auth) o `digest`.
                                 * - La scelta di "bearer" è motivata dal fatto che JWT è progettato per essere
                                 * incluso in header HTTP come "Authorization: Bearer {token}".
                                 */
                                .scheme("bearer")

                                /**
                                 * Specifica il formato del token come JWT (JSON Web Token).
                                 * - Alternativa: Se si usassero altri tipi di token, si potrebbe specificare
                                 * "OAuth", "SAML", ecc.
                                 * - La scelta di "JWT" è dovuta alla natura stateless e alla capacità del token
                                 * di contenere informazioni sull'utente.
                                 */
                                .bearerFormat("JWT")

                                /**
                                 * Fornisce una descrizione utile per gli sviluppatori che consultano la
                                 * documentazione Swagger.
                                 * Spiega come inserire correttamente il token nelle richieste.
                                 */
                                .description("Inserire il token JWT nel formato: Bearer {token}")));
    }
}

package capstone.be.auth;

import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

/**
 * Configura la sicurezza dell'applicazione con autenticazione JWT.
 *
 * **Caratteristiche:**
 * - **CORS configurato** per abilitare richieste cross-origin.
 * - **CSRF disabilitato** per operare in modalità stateless.
 * - **Autorizzazioni granulari** definite da una mappa di URL e metodi HTTP.
 * - **JWT filter** per autenticare le richieste con token.
 * - **Gestione degli errori personalizzata** con `JwtAuthenticationEntryPoint`.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;

    /**
     * Definisce gli endpoint pubblici con i relativi metodi HTTP che non richiedono
     * autenticazione.
     *
     * @return Mappa delle URL e dei metodi consentiti senza autenticazione.
     *
     *         Alternativa:
     *         - Questa mappa può essere caricata dinamicamente da un file di
     *         configurazione `.yml` o `.properties`.
     */
    @Bean
    public Map<String, List<HttpMethod>> publicUrls() {
        return Map.of(
                "/api/users/authentication", List.of(HttpMethod.POST), // Autenticazione e registrazione pubbliche
                // Tutti i metodi pubblici
                "/public/**", List.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE),
                "/swagger-ui/**", List.of(HttpMethod.GET), // Swagger accessibile pubblicamente
                "/v3/api-docs/**", List.of(HttpMethod.GET), // API docs pubbliche
                "/error", List.of(HttpMethod.GET), // Error handler pubblico
                "/sw.js", List.of(HttpMethod.GET) // Service Worker pubblico
        );
    }

    /**
     * Configura la catena di filtri di sicurezza per l'applicazione.
     *
     * @param http       Oggetto {@link HttpSecurity} da configurare.
     * @param publicUrls Mappa contenente gli URL pubblici e i metodi consentiti
     *                   senza autenticazione.
     * @return La catena di filtri configurata.
     * @throws Exception se la configurazione fallisce.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, Map<String, List<HttpMethod>> publicUrls)
            throws Exception {
        http
                // Disabilita la protezione CSRF (non necessaria per API stateless)
                .csrf(csrf -> csrf.disable())

                // Abilita la gestione delle richieste CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configura le autorizzazioni per gli endpoint
                .authorizeHttpRequests(auth -> {
                    // Applica le regole definite nella mappa publicUrls
                    publicUrls.forEach(
                            (url, methods) -> methods.forEach(method -> auth.requestMatchers(method, url).permitAll()));

                    // Tutte le altre richieste richiedono autenticazione
                    auth.anyRequest().authenticated();
                })

                // Configura la gestione delle eccezioni per gli utenti non autenticati
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // Configura il sistema di gestione delle sessioni per JWT (stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Aggiunge il filtro JWT per intercettare e validare i token prima dell'auth di
        // Spring Security
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura le impostazioni CORS per l'applicazione.
     *
     * @return Una configurazione CORS che consente richieste da qualsiasi origine.
     *
     *         **Alternativa:**
     *         - È consigliato restringere `setAllowedOrigins()` ai domini
     *         attendibili in produzione.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // Permette richieste da qualsiasi origine
        configuration.addAllowedMethod("*"); // Permette tutti i metodi HTTP (GET, POST, PUT, DELETE, etc.)
        configuration.addAllowedHeader("*"); // Permette qualsiasi header nella richiesta
        configuration.setAllowCredentials(false); // Per sicurezza, disabilita credenziali se tutte le origini sono
                                                  // permesse

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configura il password encoder per l'applicazione.
     *
     * @return Un'istanza di {@link BCryptPasswordEncoder}.
     *
     *         **Alternativa:**
     *         - `PBKDF2`, `SCrypt`, `Argon2` sono più resistenti agli attacchi
     *         brute-force.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura l'AuthenticationManager per gestire l'autenticazione.
     *
     * @param authenticationConfiguration Configurazione di autenticazione.
     * @return L'istanza di AuthenticationManager.
     * @throws Exception se la configurazione fallisce.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

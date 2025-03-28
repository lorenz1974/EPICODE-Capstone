package capstone.be.utility;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GrantedAuthorityDeserializer extends JsonDeserializer<Set<GrantedAuthority>> {

    @Override
    public Set<GrantedAuthority> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Legge il valore JSON come lista di oggetti
        List<?> rawAuthorities = p.readValueAs(new TypeReference<List<?>>() {
        });

        for (Object obj : rawAuthorities) {
            if (obj instanceof String role) {
                authorities.add(new SimpleGrantedAuthority(role));
            } else if (obj instanceof java.util.Map<?, ?> map) {
                Object authority = map.get("authority");
                if (authority instanceof String authStr && !authStr.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority(authStr));
                }
            }
        }

        return authorities;
    }
}

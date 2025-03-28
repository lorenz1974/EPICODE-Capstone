package capstone.be.appuser;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import capstone.be.doctemplate.DocTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserAuthResponse {

    private String token;

    private Long userId;
    private String username;
    private String email;
    private String name;
    private String surname;
    private String nameSurname;
    private Set<AppUserRole> roles;
    @JsonIncludeProperties({ "id", "name", "description" })
    private List<DocTemplate> docTemplatesAllowed;
}
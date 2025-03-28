package capstone.be.mail;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;

import lombok.RequiredArgsConstructor;

import capstone.be.mail.MailObject;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final GraphService graphService;

    @PostMapping("/graph/")
    @Secured("isAuthenticated()")
    public ResponseEntity<MailResponse> sendMailGraph(@RequestBody MailObject mailObject) {
        MailResponse response = graphService.sendMail(mailObject);
        return ResponseEntity.ok(response);
    }
}

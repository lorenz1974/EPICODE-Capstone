package capstone.be.utility;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

@Component
public class FakeFiscalCode {

    private final Faker faker = new Faker();

    @Bean
    @Scope("prototype")
    public String generate() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        StringBuilder fiscalCode = new StringBuilder();

        // CBBNTN
        for (int i = 0; i < 6; i++) {
            fiscalCode.append(chars.charAt(faker.random().nextInt(chars.length())));
        }
        // 88
        for (int i = 0; i < 2; i++) {
            fiscalCode.append(numbers.charAt(faker.random().nextInt(numbers.length())));
        }
        // L
        fiscalCode.append(chars.charAt(faker.random().nextInt(chars.length())));
        // 08
        for (int i = 0; i < 2; i++) {
            fiscalCode.append(numbers.charAt(faker.random().nextInt(numbers.length())));
        }
        // Z
        fiscalCode.append(chars.charAt(faker.random().nextInt(chars.length())));
        // 765
        for (int i = 0; i < 3; i++) {
            fiscalCode.append(numbers.charAt(faker.random().nextInt(numbers.length())));
        }
        // C
        fiscalCode.append(chars.charAt(faker.random().nextInt(chars.length())));

        return fiscalCode.toString();
    }
}

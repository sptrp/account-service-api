package account.util.websecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptEncoder {
    @Bean("encoder")
    public BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package account.util.websecurity;

import account.util.handlers.CustomAccessDeniedHandler;
import account.util.handlers.CustomAuthenticationEntryPoint;
import account.util.handlers.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("encoder")
    BCryptPasswordEncoder encoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService) // user DB
                .passwordEncoder(encoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(customAuthenticationEntryPoint).and()
                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler).and()
                .csrf().disable()
                .authorizeRequests()
                .mvcMatchers("/actuator/shutdown").permitAll().and()
                .authorizeRequests()
                .mvcMatchers("/api/auth/signup").permitAll().and()
                .authorizeRequests()
                .mvcMatchers("/api/auth/changepass").hasAnyRole("ADMINISTRATOR", "USER", "ACCOUNTANT").and()
                .authorizeRequests()
                .mvcMatchers("/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT").and()
                .authorizeRequests()
                .mvcMatchers("/api/admin/**").hasAnyRole("ADMINISTRATOR").and()
                .authorizeRequests()
                .mvcMatchers("/api/acct/payments").hasAnyRole("ACCOUNTANT").and()
                .authorizeRequests()
                .mvcMatchers("/api/security/events").hasAnyRole("AUDITOR")
                .anyRequest().authenticated();
    }
}

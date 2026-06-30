package holyflame.administration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/inscription", "/h2-console/**", "/css/**", "/js/**", "/fonts/**", "/images/**", "/uploads/**", "/webjars/**").permitAll()
                .requestMatchers("/super-admin/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/secretariat/**").hasAnyRole("ADMIN", "SECRETAIRE")
                .requestMatchers("/tresorerie/**").hasAnyRole("ADMIN", "TRESORIER")
                .requestMatchers("/gestion-classes/**").hasRole("ADMIN")
                .requestMatchers("/gestion-salles/**").hasRole("ADMIN")
                .requestMatchers("/matieres/**").hasRole("ADMIN")
                .requestMatchers("/personnel/**").hasRole("ADMIN")
                .requestMatchers("/surveillance/**").hasAnyRole("ADMIN", "ENSEIGNANT")
                .requestMatchers("/notes/**").hasAnyRole("ADMIN", "ENSEIGNANT", "SECRETAIRE")
                .requestMatchers("/bulletins/**").hasAnyRole("ADMIN", "ENSEIGNANT", "SECRETAIRE")
                .requestMatchers("/export/**").hasAnyRole("ADMIN", "TRESORIER")
                .requestMatchers("/parametres/**").hasRole("ADMIN")
                .requestMatchers("/tableau-enseignant/**").hasAnyRole("ADMIN", "ENSEIGNANT")
                .requestMatchers("/tableau-eleve/**").hasAnyRole("ADMIN", "ELEVE")
                .requestMatchers("/budget/**").hasAnyRole("ADMIN", "TRESORIER")
                .requestMatchers("/inventaire/**").hasRole("ADMIN")
                .requestMatchers("/rh/**").hasRole("ADMIN")
                .requestMatchers("/communication/**").hasAnyRole("ADMIN", "SECRETAIRE")
                // /direction/** ouvert à tout authentifié — contrôle fin dans le controller
                .requestMatchers("/direction/**").authenticated()
                .requestMatchers("/archives/**").hasAnyRole("ADMIN", "SECRETAIRE")
                .requestMatchers("/publications/**").hasAnyRole("ADMIN", "SECRETAIRE")
                .requestMatchers("/portail/**").hasAnyRole("ADMIN", "ELEVE", "SECRETAIRE")
                .requestMatchers("/finances/**").hasAnyRole("ADMIN", "TRESORIER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .headers(h -> h.frameOptions(fo -> fo.sameOrigin()))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

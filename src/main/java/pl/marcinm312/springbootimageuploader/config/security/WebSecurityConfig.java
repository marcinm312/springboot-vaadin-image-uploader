package pl.marcinm312.springbootimageuploader.config.security;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private static final String ADMIN_ROLE = "ADMIN";
	private static final String USER_ROLE = "USER";

	@Bean
	public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {

		http.authorizeRequests()
				.antMatchers("/upload").hasRole(ADMIN_ROLE)
				.antMatchers("/management").hasRole(ADMIN_ROLE)
				.antMatchers("/gallery").hasAnyRole(ADMIN_ROLE, USER_ROLE)
				.antMatchers("/myprofile/**").hasAnyRole(ADMIN_ROLE, USER_ROLE)

				.and().formLogin().permitAll()
				.and().logout().permitAll().logoutSuccessUrl("/")
				.and().exceptionHandling().accessDeniedPage("/forbidden")
				.and().csrf().disable()
				.sessionManagement().maximumSessions(10000).maxSessionsPreventsLogin(false)
				.expiredUrl("/").sessionRegistry(sessionRegistry());
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}

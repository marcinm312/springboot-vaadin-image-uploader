package pl.marcinm312.springbootimageuploader.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.model.enums.Role;
import pl.marcinm312.springbootimageuploader.user.repository.UserRepo;

@RequiredArgsConstructor
@Slf4j
@Service
public class FirstUserCreator {

	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;
	private final Environment environment;

	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	public UserEntity createFirstUser() {

		String login = "admin";
		if (userRepo.findByUsername(login).isEmpty()) {

			String password = environment.getProperty("admin.default.password");
			String email = environment.getProperty("admin.default.email");

			UserEntity user = UserEntity.builder()
					.username(login)
					.password(passwordEncoder.encode(password))
					.role(Role.ROLE_ADMIN)
					.enabled(true)
					.accountNonLocked(true)
					.email(email)
					.build();

			UserEntity savedUser = userRepo.save(user);
			log.info("First user created");
			return savedUser;
		}

		return null;
	}
}

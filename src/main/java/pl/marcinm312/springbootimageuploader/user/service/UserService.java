package pl.marcinm312.springbootimageuploader.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.marcinm312.springbootimageuploader.config.security.utils.SessionUtils;
import pl.marcinm312.springbootimageuploader.image.repository.ImageRepo;
import pl.marcinm312.springbootimageuploader.shared.mail.MailService;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.exception.TokenNotFoundException;
import pl.marcinm312.springbootimageuploader.user.model.ActivationTokenEntity;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.model.enums.Role;
import pl.marcinm312.springbootimageuploader.user.repository.ActivationTokenRepo;
import pl.marcinm312.springbootimageuploader.user.repository.UserRepo;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;
	private final Environment environment;
	private final ActivationTokenRepo activationTokenRepo;
	private final MailService mailService;
	private final SessionUtils sessionUtils;
	private final ImageRepo imageRepo;

	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	public UserEntity createFirstUser() {

		String login = "admin";
		if (userRepo.findByUsername(login).isEmpty()) {
			log.info("Creating administrator user");
			String password = environment.getProperty("admin.default.password");
			String email = environment.getProperty("admin.default.email");
			UserEntity userAdmin = new UserEntity(login, password, Role.ROLE_ADMIN, email);
			return createUser(userAdmin, true);
		}
		log.info("Administrator already exists in DB");
		return null;
	}

	public Optional<UserEntity> getOptionalUserByUsername(String username) {
		return userRepo.findByUsername(username);
	}

	@Transactional
	public UserEntity createUser(UserEntity user, boolean isFirstUser) {

		log.info("Creating user: {}", user);
		UserEntity savedUser;
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if (isFirstUser) {
			user.setEnabled(true);
			savedUser = userRepo.save(user);
		} else {
			user.setEnabled(false);
			savedUser = userRepo.save(user);
			sendToken(user);
		}
		log.info("User: {} created", user.getUsername());
		return savedUser;
	}

	public UserEntity updateUserData(String oldLogin, UserEntity newUser) {

		log.info("Updating user data");
		log.info("New user = {}", newUser);
		UserEntity savedUser = userRepo.save(newUser);
		if (!oldLogin.equals(newUser.getUsername())) {
			sessionUtils.expireUserSessions(oldLogin, true);
		}
		log.info("User data updated");
		return savedUser;
	}

	public UserEntity updateUserPassword(UserEntity newUser) {

		log.info("Updating user password");
		newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
		log.info("New user = {}", newUser);
		UserEntity savedUser = userRepo.save(newUser);
		sessionUtils.expireUserSessions(newUser.getUsername(), true);
		log.info("User password updated");
		return savedUser;
	}

	private void sendToken(UserEntity user) {

		String tokenValue = UUID.randomUUID().toString();
		ActivationTokenEntity token = new ActivationTokenEntity(tokenValue, user);
		activationTokenRepo.save(token);
		String emailContent = generateEmailContent(user, tokenValue);
		mailService.sendMail(user.getEmail(), "Confirm your email address", emailContent, true);
	}

	@Transactional
	public UserEntity activateUser(String tokenValue) {

		log.info("Token value = {}", tokenValue);
		Optional<ActivationTokenEntity> optionalToken = activationTokenRepo.findByValue(tokenValue);
		if (optionalToken.isEmpty()) {
			log.error("Token with value: {} not found!", tokenValue);
			throw new TokenNotFoundException();
		}
		ActivationTokenEntity token = optionalToken.get();
		UserEntity user = token.getUser();
		log.info("Activating user = {}", user.getUsername());
		user.setEnabled(true);
		UserEntity savedUser = userRepo.save(user);
		activationTokenRepo.delete(token);
		log.info("User {} activated", user.getUsername());
		return savedUser;
	}

	@Transactional
	public void deleteUser(UserEntity user) {

		log.info("Deleting user = {}", user.getUsername());
		imageRepo.deleteUserFromImages(user);
		userRepo.delete(user);
		log.info("User {} deleted", user.getUsername());
		log.info("Expiring sessions for user: {}", user.getUsername());
		sessionUtils.expireUserSessions(user.getUsername(), true);
	}

	public void expireOtherUserSessions(UserEntity user) {

		log.info("Expiring other sessions for user: {}", user.getUsername());
		sessionUtils.expireUserSessions(user.getUsername(), false);
	}

	public boolean isPasswordCorrect(UserEntity user, String currentPassword) {
		return passwordEncoder.matches(currentPassword, user.getPassword());
	}

	private String generateEmailContent(UserEntity user, String tokenValue) {

		String mailTemplate =
				"""
						Welcome %s,<br>
						<br>Confirm your email address by clicking on the link below:
						<br><a href="%s/token?value=%s">Activate account</a>""";
		return String.format(mailTemplate, user.getUsername(), VaadinUtils.getUriString(), tokenValue);
	}
}

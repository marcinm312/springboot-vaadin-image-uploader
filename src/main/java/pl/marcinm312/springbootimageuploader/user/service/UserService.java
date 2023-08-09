package pl.marcinm312.springbootimageuploader.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.marcinm312.springbootimageuploader.config.security.utils.SessionUtils;
import pl.marcinm312.springbootimageuploader.image.repository.ImageRepo;
import pl.marcinm312.springbootimageuploader.shared.mail.MailService;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;
import pl.marcinm312.springbootimageuploader.user.exception.TokenNotFoundException;
import pl.marcinm312.springbootimageuploader.user.model.ActivationTokenEntity;
import pl.marcinm312.springbootimageuploader.user.model.MailChangeTokenEntity;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.model.dto.UserCreate;
import pl.marcinm312.springbootimageuploader.user.model.dto.UserDataUpdate;
import pl.marcinm312.springbootimageuploader.user.model.dto.UserPasswordUpdate;
import pl.marcinm312.springbootimageuploader.user.model.enums.Role;
import pl.marcinm312.springbootimageuploader.user.repository.ActivationTokenRepo;
import pl.marcinm312.springbootimageuploader.user.repository.MailChangeTokenRepo;
import pl.marcinm312.springbootimageuploader.user.repository.UserRepo;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;
	private final ActivationTokenRepo activationTokenRepo;
	private final MailChangeTokenRepo mailChangeTokenRepo;
	private final MailService mailService;
	private final SessionUtils sessionUtils;
	private final ImageRepo imageRepo;

	enum MailType {
		ACTIVATION,
		MAIL_CHANGE
	}

	public Optional<UserEntity> getUserByUsername(String username) {
		return userRepo.findByUsername(username);
	}

	@Transactional
	public UserEntity createUser(UserCreate userCreate) {

		UserEntity user = UserEntity.builder()
				.username(userCreate.getUsername())
				.password(passwordEncoder.encode(userCreate.getPassword()))
				.email(userCreate.getEmail())
				.enabled(false)
				.role(Role.ROLE_USER)
				.build();

		log.info("Creating user: {}", user);
		UserEntity savedUser = userRepo.save(user);
		sendActivationToken(user);
		log.info("User: {} created", user.getUsername());
		return savedUser;
	}

	public UserEntity updateUserData(UserDataUpdate userDataUpdate, UserEntity loggedUser) {

		log.info("Updating user data");
		log.info("Old user = {}", loggedUser);
		if (!loggedUser.getUsername().equals(userDataUpdate.getUsername())) {
			log.info("Login change");
			sessionUtils.expireUserSessions(loggedUser.getUsername(), true);
			loggedUser.setUsername(userDataUpdate.getUsername());
		}
		if (loggedUser.getEmail() == null || !loggedUser.getEmail().equals(userDataUpdate.getEmail())) {
			log.info("Mail change");
			sendMailChangeToken(loggedUser, userDataUpdate.getEmail());
		}
		log.info("New user = {}", loggedUser);
		UserEntity savedUser = userRepo.save(loggedUser);
		log.info("User data updated");
		return savedUser;
	}

	public UserEntity updateUserPassword(UserPasswordUpdate userPasswordUpdate, UserEntity loggedUser) {

		log.info("Updating user password");
		log.info("Old user = {}", loggedUser);
		loggedUser.setPassword(passwordEncoder.encode(userPasswordUpdate.getPassword()));
		sessionUtils.expireUserSessions(loggedUser.getUsername(), true);
		log.info("New user = {}", loggedUser);
		UserEntity savedUser = userRepo.save(loggedUser);
		log.info("User password updated");
		return savedUser;
	}

	private void sendActivationToken(UserEntity user) {

		String tokenValue = UUID.randomUUID().toString();
		ActivationTokenEntity token = new ActivationTokenEntity(tokenValue, user);
		activationTokenRepo.save(token);
		String emailContent = generateActivationEmailContent(user, tokenValue);
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

	private String generateActivationEmailContent(UserEntity user, String tokenValue) {

		String mailTemplate =
				"""
						Welcome %s,<br>
						<br>Confirm your email address by clicking on the link below:
						<br><a href="%s">Activate account</a>""";
		return String.format(mailTemplate, user.getUsername(), getTokenUrl(MailType.ACTIVATION, tokenValue));
	}

	private String getTokenUrl(MailType mailType, String tokenValue) {

		String applicationUrl = VaadinUtils.getApplicationUrl();
		String tokenUrl;
		switch(mailType) {
			case ACTIVATION -> tokenUrl = applicationUrl + "token?value=" + tokenValue;
			case MAIL_CHANGE -> tokenUrl = applicationUrl + "myprofile/update/confirm?value=" + tokenValue;
			default -> tokenUrl = "";
		}
		return tokenUrl;
	}

	private void sendMailChangeToken(UserEntity user, String newEmail) {

		String tokenValue = UUID.randomUUID().toString();
		MailChangeTokenEntity token = new MailChangeTokenEntity(tokenValue, newEmail, user);
		mailChangeTokenRepo.save(token);
		String emailContent = generateMailChangeEmailContent(user, tokenValue);
		mailService.sendMail(newEmail, "Confirm your new email address", emailContent, true);
	}

	private String generateMailChangeEmailContent(UserEntity user, String tokenValue) {

		String mailTemplate =
				"""
						Welcome %s,<br>
						<br>Confirm your new email address by clicking on the link below:
						<br><a href="%s">I confirm the change of the email address</a>""";
		return String.format(mailTemplate, user.getUsername(), getTokenUrl(MailType.MAIL_CHANGE, tokenValue));
	}
}

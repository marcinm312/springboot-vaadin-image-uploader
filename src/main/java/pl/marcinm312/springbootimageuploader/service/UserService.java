package pl.marcinm312.springbootimageuploader.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.marcinm312.springbootimageuploader.exception.TokenNotFoundException;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.Token;
import pl.marcinm312.springbootimageuploader.repo.AppUserRepo;
import pl.marcinm312.springbootimageuploader.repo.ImageRepo;
import pl.marcinm312.springbootimageuploader.repo.TokenRepo;
import pl.marcinm312.springbootimageuploader.utils.SessionUtils;

import javax.mail.MessagingException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

	private final AppUserRepo appUserRepo;
	private final PasswordEncoder passwordEncoder;
	private final Environment environment;
	private final TokenRepo tokenRepo;
	private final MailService mailService;
	private final SessionUtils sessionUtils;
	private final ImageRepo imageRepo;

	private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public UserService(AppUserRepo appUserRepo, PasswordEncoder passwordEncoder, Environment environment,
					   TokenRepo tokenRepo, MailService mailService, SessionUtils sessionUtils, ImageRepo imageRepo) {
		this.appUserRepo = appUserRepo;
		this.passwordEncoder = passwordEncoder;
		this.environment = environment;
		this.tokenRepo = tokenRepo;
		this.mailService = mailService;
		this.sessionUtils = sessionUtils;
		this.imageRepo = imageRepo;
	}

	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	public AppUser createFirstUser() {
		if (!appUserRepo.findByUsername("administrator").isPresent()) {
			log.info("Creating administrator user");
			String password = environment.getProperty("admin.default.password");
			String email = environment.getProperty("admin.default.email");
			AppUser appUserAdmin = new AppUser("administrator", password, "ROLE_ADMIN", email);
			return createUser(appUserAdmin, true, null);
		} else {
			log.info("Administrator already exists in DB");
			return null;
		}
	}

	public Optional<AppUser> getUserByUsername(String username) {
		return appUserRepo.findByUsername(username);
	}

	@Transactional
	public AppUser createUser(AppUser appUser, boolean isFirstUser, String appURL) {
		log.info("Creating user: {}", appUser);
		AppUser savedUser;
		appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
		if (isFirstUser) {
			appUser.setEnabled(true);
			savedUser = appUserRepo.save(appUser);
		} else {
			appUser.setEnabled(false);
			savedUser = appUserRepo.save(appUser);
			sendToken(appUser, appURL);
		}
		log.info("User: {} created", appUser.getUsername());
		return savedUser;
	}

	public AppUser updateUserData(String oldLogin, AppUser newUser) {
		log.info("Updating user data");
		log.info("New user = {}", newUser);
		AppUser savedUser = appUserRepo.save(newUser);
		if (!oldLogin.equals(newUser.getUsername())) {
			sessionUtils.expireUserSessions(oldLogin, true);
			sessionUtils.expireUserSessions(newUser.getUsername(), true);
		}
		log.info("User updated");
		return savedUser;
	}

	public AppUser updateUserPassword(AppUser newUser) {
		log.info("Updating user password");
		newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
		log.info("New user = {}", newUser);
		AppUser savedUser = appUserRepo.save(newUser);
		sessionUtils.expireUserSessions(newUser.getUsername(), true);
		log.info("User updated");
		return savedUser;
	}

	public AppUser getUserByAuthentication(Authentication authentication) {
		String userName = authentication.getName();
		log.info("Loading user by authentication name = {}", userName);
		Optional<AppUser> optionalUser = appUserRepo.findByUsername(userName);
		return optionalUser.orElse(null);
	}

	private void sendToken(AppUser appUser, String appURL) {
		String tokenValue = UUID.randomUUID().toString();
		Token token = new Token();
		token.setUser(appUser);
		token.setValue(tokenValue);
		tokenRepo.save(token);
		String emailContent = generateEmailContent(appUser, tokenValue, appURL);
		try {
			mailService.sendMail(appUser.getEmail(), "Confirm your email address", emailContent, true);
		} catch (MessagingException e) {
			log.error("An error occurred while sending the email. [MESSAGE]: {}", e.getMessage());
		}
	}

	@Transactional
	public AppUser activateUser(String tokenValue) {
		Optional<Token> optionalToken = tokenRepo.findByValue(tokenValue);
		if (optionalToken.isPresent()) {
			Token token = optionalToken.get();
			AppUser appUser = token.getUser();
			log.info("Activating user = {}", appUser.getUsername());
			appUser.setEnabled(true);
			AppUser savedAppUser = appUserRepo.save(appUser);
			tokenRepo.delete(token);
			log.info("User {} activated", appUser.getUsername());
			return savedAppUser;
		} else {
			throw new TokenNotFoundException();
		}
	}

	@Transactional
	public void deleteUser(AppUser appUser) {
		log.info("Deleting user = {}", appUser.getUsername());
		imageRepo.deleteUserFromImages(appUser);
		appUserRepo.delete(appUser);
		log.info("User {} deleted", appUser.getUsername());
		log.info("Expiring sessions for user: {}", appUser.getUsername());
		sessionUtils.expireUserSessions(appUser.getUsername(), true);
	}

	public void expireOtherUserSessions(AppUser appUser) {
		log.info("Expiring other sessions for user: {}", appUser.getUsername());
		sessionUtils.expireUserSessions(appUser.getUsername(), false);
	}

	public boolean isPasswordCorrect(AppUser appUser, String currentPassword) {
		return passwordEncoder.matches(currentPassword, appUser.getPassword());
	}

	private String generateEmailContent(AppUser appUser, String tokenValue, String appURL) {
		return new StringBuilder().append("Welcome ").append(appUser.getUsername())
				.append(",<br><br>Confirm your email address by clicking on the link below:")
				.append("<br><a href=\"").append(appURL).append("/token?value=").append(tokenValue)
				.append("\">Activate account</a>").toString();
	}
}

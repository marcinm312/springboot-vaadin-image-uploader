package pl.marcinm312.springbootimageuploader.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.repo.AppUserRepo;

import java.util.Optional;

@Service
public class UserService {

	@Value("${admin.default.password}")
	private String adminDefaultPassword;

	private final AppUserRepo appUserRepo;
	private final PasswordEncoder passwordEncoder;

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	public UserService(AppUserRepo appUserRepo, PasswordEncoder passwordEncoder) {
		this.appUserRepo = appUserRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@EventListener(ApplicationReadyEvent.class)
	public AppUser createFirstUser() {
		if (!appUserRepo.findByUsername("administrator").isPresent()) {
			log.info("Creating administrator user");
			AppUser appUserAdmin = new AppUser("administrator", adminDefaultPassword, "ROLE_ADMIN");
			return createUser(appUserAdmin);
		} else {
			log.info("Administrator already exists in DB");
			return null;
		}
	}

	public Optional<AppUser> getUserByUsername(String username) {
		return appUserRepo.findByUsername(username);
	}

	public AppUser createUser(AppUser appUser) {
		log.info("Creating user: " + appUser.getUsername());
		appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
		return appUserRepo.save(appUser);
	}
}

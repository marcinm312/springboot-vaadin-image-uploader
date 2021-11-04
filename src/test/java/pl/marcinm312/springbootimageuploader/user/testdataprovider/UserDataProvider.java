package pl.marcinm312.springbootimageuploader.user.testdataprovider;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.marcinm312.springbootimageuploader.user.model.AppUser;

public class UserDataProvider {

	public static AppUser prepareExampleGoodAdministrator() {
		return new AppUser(1L, "administrator", "password", "ROLE_ADMIN", true, "aaa@abc.pl");
	}

	public static AppUser prepareExampleGoodUser() {
		return new AppUser(2L, "username", "password", "ROLE_USER", true, "bbb@abc.pl");
	}

	public static AppUser prepareExampleUserWithNullEmail() {
		return new AppUser(4L, "username4", "password", "ROLE_USER", false, null);
	}

	public static AppUser prepareExampleGoodAdministratorWithEncodedPassword() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return new AppUser(1L, "administrator", passwordEncoder.encode("password"), "ROLE_ADMIN", true, "aaa@abc.pl");
	}

	public static AppUser prepareExampleGoodUserWithEncodedPassword() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return new AppUser(2L, "username", passwordEncoder.encode("password"), "ROLE_USER", true, "bbb@abc.pl");
	}

	public static AppUser prepareExampleDisabledUserWithEncodedPassword() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return new AppUser(3L, "username3", passwordEncoder.encode("password"), "ROLE_USER", false, "bbb@abc.pl");
	}

	public static AppUser prepareExampleActivatedUserWithEncodedPassword() {
		AppUser activatedAppUser = prepareExampleDisabledUserWithEncodedPassword();
		activatedAppUser.setEnabled(true);
		return activatedAppUser;
	}
}

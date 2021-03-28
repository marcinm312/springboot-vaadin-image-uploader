package pl.marcinm312.springbootimageuploader.testdataprovider;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.marcinm312.springbootimageuploader.model.AppUser;

public class UserDataProvider {

	public static AppUser prepareExampleGoodAdministrator() {
		return new AppUser(1L, "administrator", "password", "ROLE_ADMIN", true, "aaa@abc.pl");
	}

	public static AppUser prepareExampleGoodUser() {
		return new AppUser(2L, "username", "password", "ROLE_USER", true, "bbb@abc.pl");
	}

	public static AppUser prepareExampleGoodAdministratorWithEncodedPassword() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return new AppUser(1L, "administrator", passwordEncoder.encode("password"), "ROLE_ADMIN", true, "aaa@abc.pl");
	}

	public static AppUser prepareExampleGoodUserWithEncodedPassword() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return new AppUser(2L, "username", passwordEncoder.encode("password"), "ROLE_USER", true, "bbb@abc.pl");
	}
}

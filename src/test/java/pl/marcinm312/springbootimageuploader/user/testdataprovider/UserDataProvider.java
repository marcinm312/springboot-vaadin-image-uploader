package pl.marcinm312.springbootimageuploader.user.testdataprovider;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;

public class UserDataProvider {

	public static UserEntity prepareExampleGoodAdministrator() {
		return new UserEntity(1L, "administrator", "password", "ROLE_ADMIN", true, "aaa@abc.pl");
	}

	public static UserEntity prepareExampleGoodUser() {
		return new UserEntity(2L, "username", "password", "ROLE_USER", true, "bbb@abc.pl");
	}

	public static UserEntity prepareExampleUserWithNullEmail() {
		return new UserEntity(4L, "username4", "password", "ROLE_USER", false, null);
	}

	public static UserEntity prepareExampleGoodAdministratorWithEncodedPassword() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return new UserEntity(1L, "administrator", passwordEncoder.encode("password"), "ROLE_ADMIN", true, "aaa@abc.pl");
	}

	public static UserEntity prepareExampleGoodUserWithEncodedPassword() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return new UserEntity(2L, "username", passwordEncoder.encode("password"), "ROLE_USER", true, "bbb@abc.pl");
	}

	public static UserEntity prepareExampleDisabledUserWithEncodedPassword() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return new UserEntity(3L, "username3", passwordEncoder.encode("password"), "ROLE_USER", false, "bbb@abc.pl");
	}

	public static UserEntity prepareExampleActivatedUserWithEncodedPassword() {
		UserEntity activatedUser = prepareExampleDisabledUserWithEncodedPassword();
		activatedUser.setEnabled(true);
		return activatedUser;
	}
}

package pl.marcinm312.springbootimageuploader.user.testdataprovider;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.model.enums.Role;

public class UserDataProvider {

	public static UserEntity prepareExampleGoodAdministrator() {
		return buildUserEntity(1L, "administrator", "password", Role.ROLE_ADMIN, true, "aaa@abc.pl");
	}

	public static UserEntity prepareExampleGoodUser() {
		return buildUserEntity(2L, "username", "password", Role.ROLE_USER, true, "bbb@abc.pl");
	}

	public static UserEntity prepareExampleUserWithNullEmail() {
		return buildUserEntity(4L, "username4", "password", Role.ROLE_USER, false, null);
	}

	public static UserEntity prepareExampleGoodAdministratorWithEncodedPassword() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return buildUserEntity(1L, "administrator", passwordEncoder.encode("password"), Role.ROLE_ADMIN, true, "aaa@abc.pl");
	}

	public static UserEntity prepareExampleGoodUserWithEncodedPassword() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return buildUserEntity(2L, "username", passwordEncoder.encode("password"), Role.ROLE_USER, true, "bbb@abc.pl");
	}

	public static UserEntity prepareExampleDisabledUserWithEncodedPassword() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return buildUserEntity(3L, "username3", passwordEncoder.encode("password"), Role.ROLE_USER, false, "bbb@abc.pl");
	}

	public static UserEntity prepareExampleActivatedUserWithEncodedPassword() {
		UserEntity activatedUser = prepareExampleDisabledUserWithEncodedPassword();
		activatedUser.setEnabled(true);
		return activatedUser;
	}

	private static UserEntity buildUserEntity(Long id, String username, String password, Role role, boolean enabled,
											  String email) {

		return UserEntity.builder()
				.id(id)
				.username(username)
				.password(password)
				.role(role)
				.enabled(enabled)
				.email(email)
				.build();
	}
}

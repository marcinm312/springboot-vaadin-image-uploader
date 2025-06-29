package pl.marcinm312.springbootimageuploader.user.testdataprovider;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.model.enums.Role;

public class UserDataProvider {

	public static UserEntity prepareExampleGoodAdministratorWithEncodedPassword() {

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return UserEntity.builder()
				.id(1L)
				.username("admin")
				.password(passwordEncoder.encode("password"))
				.role(Role.ROLE_ADMIN)
				.enabled(true)
				.accountNonLocked(true)
				.email("aaa@abc.pl")
				.build();
	}

	public static UserEntity prepareExampleGoodUserWithEncodedPassword() {

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return UserEntity.builder()
				.id(2L)
				.username("username")
				.password(passwordEncoder.encode("password"))
				.role(Role.ROLE_USER)
				.enabled(true)
				.accountNonLocked(true)
				.email("bbb@abc.pl")
				.build();
	}

	public static UserEntity prepareExampleDisabledUserWithEncodedPassword() {

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return UserEntity.builder()
				.id(3L)
				.username("username3")
				.password(passwordEncoder.encode("password"))
				.role(Role.ROLE_USER)
				.enabled(false)
				.accountNonLocked(true)
				.email("bbb@abc.pl")
				.build();
	}

	public static UserEntity prepareExampleUserWithNullEmail() {

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return UserEntity.builder()
				.id(4L)
				.username("username4")
				.password(passwordEncoder.encode("password"))
				.role(Role.ROLE_USER)
				.enabled(true)
				.accountNonLocked(true)
				.email(null)
				.build();
	}

	public static UserEntity prepareExampleActivatedUserWithEncodedPassword() {
		UserEntity activatedUser = prepareExampleDisabledUserWithEncodedPassword();
		activatedUser.setEnabled(true);
		return activatedUser;
	}
}

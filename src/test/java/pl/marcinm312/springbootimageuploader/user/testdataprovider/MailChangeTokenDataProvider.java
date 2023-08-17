package pl.marcinm312.springbootimageuploader.user.testdataprovider;

import pl.marcinm312.springbootimageuploader.user.model.MailChangeTokenEntity;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;

public class MailChangeTokenDataProvider {

	public static MailChangeTokenEntity prepareExampleToken() {

		UserEntity user = UserDataProvider.prepareExampleGoodUserWithEncodedPassword();
		return buildTokenEntity(1L, "123456-123-123-1234", "bbb@abc.pl", user);
	}

	private static MailChangeTokenEntity buildTokenEntity(Long id, String value, String newEmail, UserEntity user) {

		return MailChangeTokenEntity.builder()
				.id(id)
				.value(value)
				.newEmail(newEmail)
				.user(user)
				.build();
	}
}

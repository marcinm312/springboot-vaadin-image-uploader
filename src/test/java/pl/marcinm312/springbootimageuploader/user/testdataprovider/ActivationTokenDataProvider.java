package pl.marcinm312.springbootimageuploader.user.testdataprovider;

import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.model.ActivationTokenEntity;

public class ActivationTokenDataProvider {

	public static ActivationTokenEntity prepareExampleToken() {

		UserEntity user = UserDataProvider.prepareExampleDisabledUserWithEncodedPassword();
		return buildTokenEntity(1L, "123456-123-123-1234", user);
	}

	private static ActivationTokenEntity buildTokenEntity(Long id, String value, UserEntity user) {

		return ActivationTokenEntity.builder()
				.id(id)
				.value(value)
				.user(user)
				.build();
	}
}

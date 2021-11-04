package pl.marcinm312.springbootimageuploader.user.testdataprovider;

import pl.marcinm312.springbootimageuploader.user.model.AppUser;
import pl.marcinm312.springbootimageuploader.user.model.Token;

public class TokenDataProvider {

	public static Token prepareExampleToken() {
		AppUser appUser = UserDataProvider.prepareExampleDisabledUserWithEncodedPassword();
		return new Token(1L, "123456-123-123-1234", appUser);
	}
}

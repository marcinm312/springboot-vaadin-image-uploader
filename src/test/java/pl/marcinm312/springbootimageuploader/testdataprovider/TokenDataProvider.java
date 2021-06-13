package pl.marcinm312.springbootimageuploader.testdataprovider;

import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.Token;

public class TokenDataProvider {

	public static Token prepareExampleToken() {
		AppUser appUser = UserDataProvider.prepareExampleDisabledUserWithEncodedPassword();
		return new Token(1L, "123456-123-123-1234", appUser);
	}
}

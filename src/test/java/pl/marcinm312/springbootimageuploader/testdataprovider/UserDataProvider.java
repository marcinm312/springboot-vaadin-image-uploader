package pl.marcinm312.springbootimageuploader.testdataprovider;

import pl.marcinm312.springbootimageuploader.model.AppUser;

public class UserDataProvider {

	public static AppUser prepareExampleGoodAdministrator() {
		return new AppUser(1L, "administrator", "password", "ROLE_ADMIN", true, "aaa@abc.pl");
	}
}

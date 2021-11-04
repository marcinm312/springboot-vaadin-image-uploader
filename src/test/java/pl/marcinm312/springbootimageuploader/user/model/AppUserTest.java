package pl.marcinm312.springbootimageuploader.user.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;
import pl.marcinm312.springbootimageuploader.user.model.AppUser;

class AppUserTest {

	@Test
	void equalsHashCode_differentCases() {
		EqualsVerifier.forClass(AppUser.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
				.verify();
	}
}
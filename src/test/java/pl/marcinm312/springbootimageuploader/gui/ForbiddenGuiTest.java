package pl.marcinm312.springbootimageuploader.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ForbiddenGuiTest {

	@Test
	void forbiddenGuiTest_simpleCase_success() {

		ForbiddenGui forbiddenGui = new ForbiddenGui() {
			@Override
			String getAuthenticatedUsername() {
				return "username";
			}
		};
		long receivedChildrenSize = forbiddenGui.getChildren().count();
		Assertions.assertEquals(2, receivedChildrenSize);
	}
}
package pl.marcinm312.springbootimageuploader.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MainGuiTest {

	@Test
	void mainGuiTest_simpleCase_success() {

		MainGui mainGui = new MainGui();
		long receivedChildrenSize = mainGui.getChildren().count();
		Assertions.assertEquals(2, receivedChildrenSize);
	}
}
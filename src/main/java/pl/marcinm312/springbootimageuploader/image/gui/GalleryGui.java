package pl.marcinm312.springbootimageuploader.image.gui;

import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.image.model.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.image.service.ImageService;
import pl.marcinm312.springbootimageuploader.shared.utils.VaadinUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter(AccessLevel.PACKAGE)
@Slf4j
@Route("gallery")
@StyleSheet("/css/style.css")
@PageTitle("Image gallery")
public class GalleryGui extends VerticalLayout {

	private static final String MARGIN = "margin";

	private HorizontalLayout horizontalMenu;
	private Anchor logoutAnchor;
	private Anchor managementAnchor;
	private Anchor myProfileAnchor;
	private final H1 h1;

	private Carousel carousel;

	private HorizontalLayout paginationContainer;
	private Paragraph paginationText;

	private HorizontalLayout navigationButtons;
	private Button nextImageButton;
	private Button prevImageButton;
	private Button lastImageButton;
	private Button firstImageButton;

	private final transient List<ImageDto> allImagesFromDB;

	@Autowired
	public GalleryGui(ImageService imageService) {

		log.info("authentication.getName()={}", VaadinUtils.getAuthenticatedUserName());

		prepareHorizontalMenu();

		h1 = new H1("Image gallery");

		log.info("Loading all images from DB");
		allImagesFromDB = imageService.getAllImagesFromDB();
		log.info("allImagesFromDB.size()={}", allImagesFromDB.size());

		prepareImageCarousel();
		prepareImageCounter();
		prepareNavigationButtons();

		add(horizontalMenu, h1, carousel, paginationContainer, navigationButtons);
	}

	private void prepareImageCounter() {

		paginationContainer = new HorizontalLayout();
		paginationText = new Paragraph("");
		paginationText.getStyle().set(MARGIN, "0");
		paginationContainer.setAlignItems(Alignment.CENTER);
		paginationContainer.setJustifyContentMode(JustifyContentMode.CENTER);
		paginationContainer.setWidthFull();
		paginationContainer.setMargin(false);
		paginationContainer.getStyle().set(MARGIN, "0");
		paginationContainer.add(paginationText);
	}

	private void prepareHorizontalMenu() {

		logoutAnchor = new Anchor("../logout", "Log out");
		managementAnchor = new Anchor("../management", "Image management");
		myProfileAnchor = new Anchor("../myprofile/update", "My profile");
		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, myProfileAnchor);
		if (VaadinUtils.isCurrentUserAdmin()) {
			horizontalMenu.add(managementAnchor);
		}
	}

	private void prepareNavigationButtons() {

		nextImageButton = new Button(">>");
		prevImageButton = new Button("<<");
		lastImageButton = new Button(">|");
		firstImageButton = new Button("|<");

		nextImageButton.addClickListener(e -> carousel.moveNext());
		prevImageButton.addClickListener(e -> carousel.movePrev());
		lastImageButton.addClickListener(e -> carousel.movePos(allImagesFromDB.size() - 1));
		firstImageButton.addClickListener(e -> carousel.movePos(0));

		navigationButtons = new HorizontalLayout(firstImageButton, prevImageButton, nextImageButton,
				lastImageButton);
		navigationButtons.setAlignItems(Alignment.CENTER);
		navigationButtons.setJustifyContentMode(JustifyContentMode.CENTER);
		navigationButtons.setWidthFull();
		navigationButtons.setMargin(false);
	}

	private void prepareImageCarousel() {

		carousel = new Carousel();
		carousel.setHideNavigation(true);
		List<Slide> slidesList = new ArrayList<>();

		for (ImageDto imageDto : allImagesFromDB) {

			Image vaadinImage = new Image(imageDto.getAutoCompressedImageAddress(), imageDto.getAutoCompressedImageAddress());
			vaadinImage.setMaxHeight("65vh");
			vaadinImage.setMaxWidth("90vw");
			vaadinImage.getStyle().set(MARGIN, "0");

			VerticalLayout verticalLayout = new VerticalLayout(vaadinImage);
			verticalLayout.setAlignItems(Alignment.CENTER);
			verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
			verticalLayout.setMargin(false);
			slidesList.add(new Slide(verticalLayout));
		}

		Slide[] slidesArray = slidesList.toArray(new Slide[0]);
		carousel.setSlides(cleanSlidesExceptOne(slidesArray, 0));
		carousel.setSizeFull();
		carousel.addChangeListener(e -> {
					int positionInt = Integer.parseInt(e.getPosition());
					carousel.getElement().removeAllChildren();
					carousel.setSlides(cleanSlidesExceptOne(slidesArray, positionInt));
					paginationText.setText(preparePaginationText(e.getPosition()));
				}
		);

		log.info("All images loaded");
	}

	private String preparePaginationText(String position) {

		int positionNumber = 0;
		try {
			positionNumber = Integer.parseInt(position);
		} catch (Exception e) {
			String errorMessage = String.format("Error converting the position=%s to int: %s", position, e.getMessage());
			log.error(errorMessage, e);
		}
		if (!allImagesFromDB.isEmpty()) {
			positionNumber++;
		}
		return "Image " + positionNumber + " of " + allImagesFromDB.size();
	}

	private Slide[] cleanSlidesExceptOne(Slide[] oldSlidesArray, int index) {

		Slide[] newSlidesArray = Arrays.copyOf(oldSlidesArray, oldSlidesArray.length);
		for (int i = 0; i < oldSlidesArray.length; i++) {
			if (i != index) {
				newSlidesArray[i] = new Slide();
			}
		}
		return newSlidesArray;
	}
}

package pl.marcinm312.springbootimageuploader.gui;

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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pl.marcinm312.springbootimageuploader.model.image.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.service.ImageService;
import pl.marcinm312.springbootimageuploader.utils.VaadinUtils;

import java.util.ArrayList;
import java.util.List;

@Route("gallery")
@StyleSheet("/css/style.css")
@PageTitle("Image gallery")
public class GalleryGui extends VerticalLayout {

	private static final String MARGIN = "margin";

	HorizontalLayout horizontalMenu;
	Anchor logoutAnchor;
	Anchor managementAnchor;
	Anchor myProfileAnchor;
	H1 h1;

	Carousel carousel;

	HorizontalLayout paginationContainer;
	Paragraph paginationText;

	HorizontalLayout navigationButtons;
	Button nextImageButton;
	Button prevImageButton;
	Button lastImageButton;
	Button firstImageButton;

	private final transient List<ImageDto> allImagesFromDB;

	private final transient org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public GalleryGui(ImageService imageService) {

		log.info("authentication.getName()={}", VaadinUtils.getAuthenticatedUserName());

		logoutAnchor = new Anchor("../logout", "Log out");
		managementAnchor = new Anchor("../management", "Image management");
		myProfileAnchor = new Anchor("../myprofile/update", "My profile");
		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, myProfileAnchor);
		if (VaadinUtils.isCurrentUserAdmin()) {
			horizontalMenu.add(managementAnchor);
		}

		h1 = new H1("Image gallery");

		log.info("Loading all images from DB");
		allImagesFromDB = imageService.getAllImagesFromDB();
		log.info("allImagesFromDB.size()={}", allImagesFromDB.size());

		prepareImageCarousel();

		paginationContainer = new HorizontalLayout();
		paginationText = new Paragraph("");
		paginationText.getStyle().set(MARGIN, "0");
		paginationContainer.setAlignItems(Alignment.CENTER);
		paginationContainer.setJustifyContentMode(JustifyContentMode.CENTER);
		paginationContainer.setWidthFull();
		paginationContainer.setMargin(false);
		paginationContainer.getStyle().set(MARGIN, "0");
		paginationContainer.add(paginationText);

		prepareNavigationButtons();

		add(horizontalMenu, h1, carousel, paginationContainer, navigationButtons);
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
		carousel = new Carousel().withoutNavigation();
		List<Slide> slidesList = new ArrayList<>();
		for (ImageDto imageDto : allImagesFromDB) {
			Image vaadinImage = new Image(imageDto.getAutoCompressedImageAddress(), imageDto.getAutoCompressedImageAddress());
			vaadinImage.setMaxHeight("65vh");
			vaadinImage.setMaxWidth("90vw");
			vaadinImage.getStyle().set(MARGIN, "0");
			VerticalLayout d = new VerticalLayout(vaadinImage);
			d.setAlignItems(Alignment.CENTER);
			d.setJustifyContentMode(JustifyContentMode.CENTER);
			d.setMargin(false);
			slidesList.add(new Slide(d));
		}
		Slide[] slidesArray = new Slide[slidesList.size()];
		slidesList.toArray(slidesArray);
		carousel.setSlides(slidesArray);
		carousel.setSizeFull();
		carousel.addChangeListener(e -> paginationText.setText(preparePaginationText(e.getPosition())));

		log.info("All images loaded");
	}

	private String preparePaginationText(String position) {
		int positionNumber = 0;
		try {
			positionNumber = Integer.parseInt(position);
		} catch (Exception e) {
			log.error("Error converting the position to int: {}", e.getMessage());
		}
		if (!allImagesFromDB.isEmpty()) {
			positionNumber ++;
		}
		return "Image " + positionNumber + " of " + allImagesFromDB.size();
	}
}

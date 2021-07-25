package pl.marcinm312.springbootimageuploader.gui;

import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.marcinm312.springbootimageuploader.model.AppUser;
import pl.marcinm312.springbootimageuploader.model.dto.ImageDto;
import pl.marcinm312.springbootimageuploader.service.ImageService;

import java.util.ArrayList;
import java.util.List;

@Route("gallery")
@StyleSheet("/css/style.css")
public class GalleryGui extends VerticalLayout {

	HorizontalLayout horizontalMenu;
	Anchor logoutAnchor;
	Anchor managementAnchor;
	Anchor myProfileAnchor;
	H1 h1;

	Carousel carousel;

	HorizontalLayout navigationButtons;
	Button nextImageButton;
	Button prevImageButton;
	Button lastImageButton;
	Button firstImageButton;
	Span paginationSpan;
	Text paginationText;

	private final transient List<ImageDto> allImagesFromDB;
	private int imageNumber = 1;

	private final transient org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public GalleryGui(ImageService imageService) {

		log.info("authentication.getName()={}", getAuthenticationName());

		logoutAnchor = new Anchor("../logout", "Log out");
		managementAnchor = new Anchor("../management", "Image management");
		myProfileAnchor = new Anchor("../myprofile/update", "My profile");
		horizontalMenu = new HorizontalLayout();
		horizontalMenu.add(logoutAnchor, myProfileAnchor);
		if (isUserAdmin()) {
			horizontalMenu.add(managementAnchor);
		}

		h1 = new H1("Image gallery");

		log.info("Loading all images from DB");
		allImagesFromDB = imageService.getAllImagesFromDB();
		log.info("allImagesFromDB.size()={}", allImagesFromDB.size());

		prepareImageCarousel();
		prepareNavigationButtons();

		add(horizontalMenu, h1, carousel, navigationButtons);
	}

	private void prepareNavigationButtons() {
		nextImageButton = new Button(">>");
		prevImageButton = new Button("<<");
		lastImageButton = new Button(">|");
		firstImageButton = new Button("|<");
		paginationText = new Text(preparePaginationText());
		nextImageButton.addClickListener(e -> {
			if (imageNumber == allImagesFromDB.size()) {
				imageNumber = 1;
			} else {
				imageNumber++;
			}
			paginationText.setText(preparePaginationText());
			carousel.moveNext();
		});
		prevImageButton.addClickListener(e -> {
			if (imageNumber == 1) {
				imageNumber = allImagesFromDB.size();
			} else {
				imageNumber--;
			}
			paginationText.setText(preparePaginationText());
			carousel.movePrev();
		});
		lastImageButton.addClickListener(e -> {
			imageNumber = allImagesFromDB.size();
			paginationText.setText(preparePaginationText());
			carousel.movePos(allImagesFromDB.size() - 1);
		});
		firstImageButton.addClickListener(e -> {
			imageNumber = 1;
			paginationText.setText(preparePaginationText());
			carousel.movePos(0);
		});
		paginationSpan = new Span();
		paginationSpan.add(paginationText);
		navigationButtons = new HorizontalLayout(firstImageButton, prevImageButton, paginationSpan, nextImageButton,
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
			vaadinImage.getStyle().set("margin", "0");
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

		log.info("All images loaded");
	}

	String getAuthenticationName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}

	boolean isUserAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return "ROLE_ADMIN".equals(((AppUser) authentication.getPrincipal()).getRole());
	}

	private String preparePaginationText() {
		return "Image " + imageNumber + " of " + allImagesFromDB.size();
	}
}

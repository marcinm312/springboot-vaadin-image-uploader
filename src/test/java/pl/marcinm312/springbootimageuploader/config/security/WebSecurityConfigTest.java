package pl.marcinm312.springbootimageuploader.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.mock.mockito.SpyBeans;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.marcinm312.springbootimageuploader.main.gui.MainGui;
import pl.marcinm312.springbootimageuploader.user.repository.UserRepo;
import pl.marcinm312.springbootimageuploader.user.service.UserDetailsServiceImpl;
import pl.marcinm312.springbootimageuploader.user.testdataprovider.UserDataProvider;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ComponentScan(basePackageClasses = MainGui.class,
		useDefaultFilters = false,
		includeFilters = {
				@ComponentScan.Filter(type = ASSIGNABLE_TYPE, value = MainGui.class)
		})
@Import({WebSecurityConfig.class})
@SpyBeans({@SpyBean(UserDetailsServiceImpl.class)})
@WebAppConfiguration
class WebSecurityConfigTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private UserRepo userRepo;

	@BeforeEach
	void setUp() {
		given(userRepo.findByUsername("admin"))
				.willReturn(Optional.of(UserDataProvider.prepareExampleGoodAdministratorWithEncodedPassword()));
		given(userRepo.findByUsername("username"))
				.willReturn(Optional.of(UserDataProvider.prepareExampleGoodUserWithEncodedPassword()));
		given(userRepo.findByUsername("lalala"))
				.willReturn(Optional.empty());

		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(this.webApplicationContext)
				.apply(springSecurity())
				.alwaysDo(print())
				.build();
	}

	@Test
	void getCss_simpleCase_success() throws Exception {
		mockMvc.perform(
						get("/css/style.css"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/css"))
				.andExpect(unauthenticated());
	}

	@Test
	void formLogin_userWithGoodCredentials_success() throws Exception {
		mockMvc.perform(
						formLogin().user("username").password("password"))
				.andExpect(authenticated().withUsername("username").withRoles("USER"));
	}

	@Test
	void formLogin_administratorWithGoodCredentials_success() throws Exception {
		mockMvc.perform(
						formLogin().user("admin").password("password"))
				.andExpect(authenticated().withUsername("admin").withRoles("ADMIN"));
	}

	@Test
	void formLogin_userWithBadCredentials_unauthenticated() throws Exception {
		mockMvc.perform(
						formLogin().user("username").password("invalid"))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(unauthenticated());
	}

	@Test
	void formLogin_administratorWithBadCredentials_unauthenticated() throws Exception {
		mockMvc.perform(
						formLogin().user("admin").password("invalid"))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(unauthenticated());
	}

	@Test
	void formLogin_notExistingUser_unauthenticated() throws Exception {
		mockMvc.perform(
						formLogin().user("lalala").password("password"))
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(unauthenticated());
	}

	@Test
	void logout_simpleCase_success() throws Exception {
		mockMvc.perform(
						logout())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(unauthenticated());
	}
}

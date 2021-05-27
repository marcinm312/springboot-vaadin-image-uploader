package pl.marcinm312.springbootimageuploader.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
public class AppUser extends AuditModel implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	@NotBlank(message = "This field must be completed!")
	@Size(min = 3, max = 50, message = "This field must contain between 3 and 50 characters")
	private String username;

	@NotBlank(message = "This field must be completed!")
	@Size(min = 6, message = "This field must contain at least 6 characters")
	private String password;

	private String role;

	private boolean isEnabled;

	@NotBlank(message = "This field must be completed!")
	@Email(message = "Incorrect email address! ")
	private String email;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "appUser")
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	private final List<Image> images = new ArrayList<>();

	public AppUser(String username, String password, String role, String email) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.email = email;
	}

	public AppUser(Long id, String username, String password, String role, boolean isEnabled, String email) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
		this.isEnabled = isEnabled;
		this.email = email;
	}

	public AppUser() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(role));
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public List<Image> getImages() {
		return images;
	}

	@Override
	public String toString() {
		return "AppUser{" +
				"username='" + username + '\'' +
				", role='" + role + '\'' +
				", isEnabled=" + isEnabled +
				", email='" + email + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AppUser appUser = (AppUser) o;
		return isEnabled == appUser.isEnabled && Objects.equals(id, appUser.id) && username.equals(appUser.username) && password.equals(appUser.password) && Objects.equals(role, appUser.role) && Objects.equals(email, appUser.email);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, username, password, role, isEnabled, email);
	}
}

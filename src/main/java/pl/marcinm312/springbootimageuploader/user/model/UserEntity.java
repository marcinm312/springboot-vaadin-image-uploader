package pl.marcinm312.springbootimageuploader.user.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.marcinm312.springbootimageuploader.shared.model.AuditModel;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "app_user")
public class UserEntity extends AuditModel implements UserDetails {

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

	public UserEntity(String username, String password, String role, String email) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.email = email;
	}

	public UserEntity(Long id, String username, String password, String role, boolean isEnabled, String email) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
		this.isEnabled = isEnabled;
		this.email = email;
	}

	public UserEntity() {
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

	@Override
	public String toString() {
		return "UserEntity{" +
				"username='" + username + '\'' +
				", role='" + role + '\'' +
				", isEnabled=" + isEnabled +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UserEntity)) return false;

		UserEntity user = (UserEntity) o;

		return getUsername() != null ? getUsername().equals(user.getUsername()) : user.getUsername() == null;
	}

	@Override
	public int hashCode() {
		return getUsername() != null ? getUsername().hashCode() : 0;
	}
}

package pl.marcinm312.springbootimageuploader.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.marcinm312.springbootimageuploader.shared.model.AuditModel;
import pl.marcinm312.springbootimageuploader.shared.model.CommonEntity;
import pl.marcinm312.springbootimageuploader.user.model.enums.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "app_user")
public class UserEntity extends AuditModel implements UserDetails, CommonEntity {

	@Id
	@GeneratedValue(generator = "user_generator")
	@SequenceGenerator(name = "user_generator", sequenceName = "app_user_id_seq", allocationSize = 1)
	private Long id;

	@Column(unique = true)
	@NotBlank(message = "This field must be completed!")
	@Size(min = 3, max = 50, message = "This field must contain between 3 and 50 characters")
	private String username;

	@NotBlank(message = "This field must be completed!")
	@Size(min = 5, message = "This field must contain at least 5 characters")
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(name = "is_enabled")
	private boolean enabled;

	@NotBlank(message = "This field must be completed!")
	@Email(message = "Incorrect email address!")
	private String email;

	public UserEntity(String username, String password, Role role, String email) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.email = email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(role.name()));
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
	public String toString() {
		return "UserEntity{" +
				"username='" + username + '\'' +
				", role='" + role + '\'' +
				", isEnabled=" + enabled +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UserEntity user)) return false;

		return getUsername() != null ? getUsername().equals(user.getUsername()) : user.getUsername() == null;
	}

	@Override
	public int hashCode() {
		return getUsername() != null ? getUsername().hashCode() : 0;
	}
}

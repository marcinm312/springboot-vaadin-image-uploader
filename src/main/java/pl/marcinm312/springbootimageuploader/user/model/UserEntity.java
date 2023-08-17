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
	private String username;

	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(name = "is_enabled")
	private boolean enabled;

	private String email;

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

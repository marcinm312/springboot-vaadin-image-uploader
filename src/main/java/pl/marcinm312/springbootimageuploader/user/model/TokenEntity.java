package pl.marcinm312.springbootimageuploader.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.marcinm312.springbootimageuploader.shared.model.AuditModel;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@SuperBuilder
@Entity
@Table(name = "tokens")
public class TokenEntity extends AuditModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String value;

	@OneToOne
	private UserEntity user;

	public TokenEntity(String value, UserEntity user) {
		this.value = value;
		this.user = user;
	}

	@Override
	public String toString() {
		return "TokenEntity{" +
				"id=" + id +
				", value='" + value + '\'' +
				", user=" + user +
				'}';
	}
}

package pl.marcinm312.springbootimageuploader.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.marcinm312.springbootimageuploader.shared.model.AuditModel;
import pl.marcinm312.springbootimageuploader.shared.model.CommonEntityWithUser;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@SuperBuilder
@Entity
@Table(name = "activation_tokens")
public class ActivationTokenEntity extends AuditModel implements CommonEntityWithUser {

	@Id
	@GeneratedValue(generator = "activation_token_generator")
	@SequenceGenerator(name = "activation_token_generator", sequenceName = "activation_token_sequence", allocationSize = 1)
	private Long id;

	private String value;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	public ActivationTokenEntity(String value, UserEntity user) {
		this.value = value;
		this.user = user;
	}

	@Override
	public String toString() {
		return "ActivationTokenEntity{" +
				"id=" + id +
				", value='" + value + '\'' +
				", user=" + user +
				'}';
	}
}

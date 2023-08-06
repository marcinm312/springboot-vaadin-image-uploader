package pl.marcinm312.springbootimageuploader.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pl.marcinm312.springbootimageuploader.shared.model.AuditModel;
import pl.marcinm312.springbootimageuploader.shared.model.CommonEntityWithUser;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "mail_change_tokens")
public class MailChangeTokenEntity extends AuditModel implements CommonEntityWithUser {

	@Id
	@GeneratedValue(generator = "mail_change_token_generator")
	@SequenceGenerator(name = "mail_change_token_generator", sequenceName = "mail_change_token_sequence", allocationSize = 1)
	private Long id;

	@NotBlank
	private String value;

	@NotBlank
	@Email(message = "Incorrect email address!")
	private String newEmail;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private UserEntity user;

	public MailChangeTokenEntity(String value, String newEmail, UserEntity user) {
		this.value = value;
		this.newEmail = newEmail;
		this.user = user;
	}

	@Override
	public String toString() {
		return "MailChangeTokenEntity [id=" + id + ", value=" + value + ", user=" + user + "]";
	}
}

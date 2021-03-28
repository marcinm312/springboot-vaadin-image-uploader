package pl.marcinm312.springbootimageuploader.utils;

import com.vaadin.flow.component.UI;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class SessionUtils {

	private final SessionRegistry sessionRegistry;

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public SessionUtils(SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}

	public void expireUserSessions(String username, boolean expireCurrentSession) {
		log.info("Starting expiring user sessions");
		for (Object principal : sessionRegistry.getAllPrincipals()) {
			if (principal instanceof UserDetails) {
				UserDetails userDetails = (UserDetails) principal;
				if (userDetails.getUsername().equals(username)) {
					for (SessionInformation sessionInformation : sessionRegistry.getAllSessions(userDetails, true)) {
						if (expireCurrentSession) {
							expireSession(username, sessionInformation);
						} else {
							String currentSessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
							if (!sessionInformation.getSessionId().equals(currentSessionId)) {
								expireSession(username, sessionInformation);
							}
						}
					}
				}
			}
		}
		log.info("User sessions expired");
	}

	private void expireSession(String username, SessionInformation sessionInformation) {
		sessionInformation.expireNow();
		UI.getCurrent().getPage().reload();
		log.info("Session " + sessionInformation.getSessionId() + " of user " + username + " has expired");
	}
}

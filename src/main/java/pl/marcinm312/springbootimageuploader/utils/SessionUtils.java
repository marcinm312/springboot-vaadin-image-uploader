package pl.marcinm312.springbootimageuploader.utils;

import com.vaadin.flow.component.UI;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

@Component
public class SessionUtils {

	private final SessionRegistry sessionRegistry;

	private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	public SessionUtils(SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}

	public void expireUserSessions(String username, boolean expireCurrentSession) {
		log.info("Starting expiring sessions for user: {}", username);
		for (Object principal : sessionRegistry.getAllPrincipals()) {
			if (principal instanceof UserDetails) {
				UserDetails userDetails = (UserDetails) principal;
				if (userDetails.getUsername().equals(username)) {
					List<SessionInformation> listOfSessionInformation = sessionRegistry.getAllSessions(userDetails, true);
					log.info("listOfSessionInformation.size()={}", listOfSessionInformation.size());
					for (SessionInformation sessionInformation : listOfSessionInformation) {
						processSession(username, expireCurrentSession, sessionInformation);
					}
				}
			}
		}
		log.info("Sessions for user: {} expired", username);
	}

	private void processSession(String username, boolean expireCurrentSession, SessionInformation sessionInformation) {
		if (expireCurrentSession) {
			expireSession(username, true, sessionInformation);
		} else {
			String currentSessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
			if (!sessionInformation.getSessionId().equals(currentSessionId)) {
				expireSession(username, false, sessionInformation);
			}
		}
	}

	private void expireSession(String username, boolean expireCurrentSession, SessionInformation sessionInformation) {
		sessionInformation.expireNow();
		if (expireCurrentSession) {
			UI.getCurrent().getPage().reload();
		}
		log.info("Session {} of user {} has expired", sessionInformation.getSessionId(), username);
	}
}

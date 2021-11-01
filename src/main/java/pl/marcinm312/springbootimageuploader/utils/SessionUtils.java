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
					processSessionsOfUser(username, expireCurrentSession, userDetails);
				}
			}
		}
		log.info("Sessions for user: {} expired", username);
	}

	private void processSessionsOfUser(String username, boolean expireCurrentSession, UserDetails userDetails) {
		List<SessionInformation> listOfSessionInformation = sessionRegistry.getAllSessions(userDetails, true);
		log.info("listOfSessionInformation.size()={}", listOfSessionInformation.size());
		if (expireCurrentSession) {
			for (SessionInformation sessionInformation : listOfSessionInformation) {
				expireSession(username, sessionInformation);
			}
			UI.getCurrent().getPage().reload();
		} else {
			String currentSessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
			for (SessionInformation sessionInformation : listOfSessionInformation) {
				if (!sessionInformation.getSessionId().equals(currentSessionId)) {
					expireSession(username, sessionInformation);
				}
			}
		}
	}

	private void expireSession(String username, SessionInformation sessionInformation) {
		sessionInformation.expireNow();
		log.info("Session {} of user {} has expired", sessionInformation.getSessionId(), username);
	}
}

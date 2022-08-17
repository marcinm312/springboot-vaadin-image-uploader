package pl.marcinm312.springbootimageuploader.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.marcinm312.springbootimageuploader.user.model.UserEntity;
import pl.marcinm312.springbootimageuploader.user.repository.UserRepo;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		log.info("Loading user: {}", username);
		Optional<UserEntity> optionalUser = userRepo.findByUsername(username);
		if (optionalUser.isEmpty()) {
			throw new UsernameNotFoundException("User not found");
		}
		return optionalUser.get();
	}
}

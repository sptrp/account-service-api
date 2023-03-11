package account.business.service;

import account.business.model.entities.User;
import account.persistence.UserRepository;
import account.util.websecurity.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByEmail(email.toLowerCase());

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Not found: " + email);
        }
        return new UserDetailsImpl(user.get());
    }
}
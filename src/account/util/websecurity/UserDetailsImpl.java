package account.util.websecurity;

import account.business.model.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
    private final String email;
    private final String password;
    private final List<GrantedAuthority> rolesAndAuthorities;
    private final boolean isAccountNonLocked;

    public UserDetailsImpl(User user) {
        List<GrantedAuthority> rolesAndAuthoritiesList = new ArrayList<>();
        for (String role : user.getRoles()) {
            rolesAndAuthoritiesList.add(new SimpleGrantedAuthority(role));
        }

        email = user.getEmail();
        password = user.getPassword();
        rolesAndAuthorities = rolesAndAuthoritiesList;
        isAccountNonLocked = user.isAccountNonLocked();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rolesAndAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    // METHODS NOT USED
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

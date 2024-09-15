package vn.huuloc.boardinghouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import vn.huuloc.boardinghouse.entity.Privilege;
import vn.huuloc.boardinghouse.entity.Role;
import vn.huuloc.boardinghouse.entity.User;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> BadRequestException.message("User is invalid."));
        String[] roles = user.getRoles().stream().map(Role::getName).toArray(String[]::new);
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .accountExpired(!user.isEnabled())
                .credentialsExpired(!user.isEnabled())
                .accountLocked(!user.isEnabled())
                .roles(roles)
                .authorities(getAuthorities(user.getRoles()))
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(final List<Role> roles) {
        return roles.stream().map(s -> new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_" + s.getName();
            }
        }).collect(Collectors.toList());
//        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(final Collection<Role> roles) {
        final List<String> privileges = new ArrayList<>();
        final List<Privilege> collection = new ArrayList<>();
        for (final Role role : roles) {
            // privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }
        for (final Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        return privileges.stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
//         List<GrantedAuthority> authorities = new ArrayList<>();
//        for (final String privilege : privileges) {
//            authorities.add(new SimpleGrantedAuthority(privilege));
//        }
//        return authorities;

    }
}

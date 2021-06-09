package br.com.devisrael.helpdesk.security;

import br.com.devisrael.helpdesk.api.entity.User;
import br.com.devisrael.helpdesk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userService.findByEmail(s);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", s));
        } else {
            return JwtUserFactory.create(user);
        }
    }
}

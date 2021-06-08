package br.com.devisrael.helpdesk.security;

import br.com.devisrael.helpdesk.api.entity.Profile;
import br.com.devisrael.helpdesk.api.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public class JwtUserFactory {

    public JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(user.getId(), user.getEmail(), user.getPassword(), mapToGrandedAuthorities(user.getProfile()));
    }

    private static Collection<? extends GrantedAuthority> mapToGrandedAuthorities(Profile profile) {
        var grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority(profile.toString()));
        return grantedAuthorities;
    }
}

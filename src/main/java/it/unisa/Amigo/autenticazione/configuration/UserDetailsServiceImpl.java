package it.unisa.Amigo.autenticazione.configuration;

import it.unisa.Amigo.autenticazione.dao.UserDAO;
import it.unisa.Amigo.autenticazione.domain.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @NonNull
    private UserDAO userDao;

    @Override
    public UserDetails loadUserByUsername(final String s) throws UsernameNotFoundException {
        User selectedUser = userDao.findByEmail(s);
        return new UserDetailImpl(selectedUser);
    }
}

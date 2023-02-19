package eu.utilo.authorization.services;

import eu.utilo.authorization.entity.OauthUser;
import eu.utilo.authorization.repository.UserRepository;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import java.util.Optional;

/**
 * User Details aus Mysql holen
 */
public class MySQLUserDetailServiceImpl implements UserDetailsManager, UserDetailsPasswordService {

    private Logger logger = LoggerFactory.getLogger(MySQLUserDetailServiceImpl.class);
    
    @Resource
    private UserRepository userRepository;

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        logger.debug("loadUserByUsername UserDetails " + username);
        
        Optional<OauthUser> userByUsername = userRepository.findUserByUsername(username);
        if (!userByUsername.isPresent()) {
            logger.warn("Benutzername nicht gefunden " + username);
            throw new UsernameNotFoundException("Benutzername nicht gefunden.");
        }
        
        return userByUsername.get();
        
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {

        logger.debug("updatePassword UserDetails " + user);
        
        Optional<OauthUser> userByUsername = userRepository.findUserByUsername(user.getUsername());
        if (userByUsername.isPresent()) {

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            newPassword = bCryptPasswordEncoder.encode(newPassword);
            
            OauthUser oauthUser = userByUsername.get();
            oauthUser.setPassword(newPassword);
            userRepository.save(oauthUser);
            return oauthUser;
            
        }
        
        return null;
        
    }

    @Override
    public void createUser(UserDetails user) {

        logger.debug("createUser UserDetails " + user);

        OauthUser ouser = new OauthUser();
        ouser.setUsername(user.getUsername());
        ouser.setPassword(user.getPassword());
        userRepository.save(ouser);

    }

    @Override
    public void updateUser(UserDetails user) {

        logger.debug("updateUser UserDetails " + user);

        OauthUser ouser = new OauthUser();
        ouser.setUsername(user.getUsername());
        ouser.setPassword(user.getPassword());
        userRepository.save(ouser);

    }

    @Override
    public void deleteUser(String username) {

        logger.debug("deleteUser UserDetails " + username);

        userRepository.deleteByUsername(username);

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

        Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {

            String message = "Can't change password as no Authentication object found in context " +
                    "for current user.";
            logger.warn(message);
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(message);

        }

        logger.debug("changePassword UserDetails " + currentUser);

        String username = currentUser.getName();
        Optional<OauthUser> userByUsername = userRepository.findUserByUsername(username);
        if (userByUsername.isPresent()) {
            OauthUser user = new OauthUser();
            user.setUsername(userByUsername.get().getUsername());
            user.setPassword(userByUsername.get().getPassword());
            userRepository.save(user);
        }

    }

    @Override
    public boolean userExists(String username) {

        logger.debug("userExists UserDetails " + username);

        Optional<OauthUser> userByUsername = userRepository.findUserByUsername(username);
        return userByUsername.isPresent();

    }

}

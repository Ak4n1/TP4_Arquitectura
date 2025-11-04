package com.tudai.monopatines.auth.security.services;

import com.tudai.monopatines.auth.model.UserResponseGrpc;
import com.tudai.monopatines.auth.security.model.UserDetailsImpl;
import com.tudai.monopatines.auth.service.AccountsServiceClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementacion de UserDetailsService para Spring Security.
 * Obtiene datos del usuario desde accounts-service mediante gRPC.
 * 
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountsServiceClient accountsServiceClient;

    public UserDetailsServiceImpl(AccountsServiceClient accountsServiceClient) {
        this.accountsServiceClient = accountsServiceClient;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            UserResponseGrpc user = accountsServiceClient.getUserByEmail(email);
            
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new UsernameNotFoundException("User password not found for email: " + email);
            }
            
            List<String> roles = user.getRoles() != null ? user.getRoles() : List.of();
            
            return UserDetailsImpl.build(
                    user.getId(),
                    user.getEmail(),
                    user.getPassword(),
                    roles
            );
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found with email: " + email, e);
        }
    }
}


package com.javasolution.app.ppmtool.services;

import com.javasolution.app.ppmtool.domain.User;
import com.javasolution.app.ppmtool.exceptions.UsernameAlreadyExistsException;
import com.javasolution.app.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser(User newUser){

        try{
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));

            //username has to be unique(exception)
            //make sure that password and confirmPassword match
            //we dont persist or show the confirmPassword
            newUser.setConfirmPassword("");
            return userRepository.save(newUser);
        }catch(Exception e){
            throw new UsernameAlreadyExistsException("Username '"+newUser.getUsername()+"' already exists");
        }

    }
}

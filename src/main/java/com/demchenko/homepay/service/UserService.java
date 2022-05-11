package com.demchenko.homepay.service;


import com.demchenko.homepay.dto.request.UserRegistryForm;
import com.demchenko.homepay.dto.request.UserUpdateForm;
import com.demchenko.homepay.entity.User;

import java.util.List;


public interface UserService {

    User findUserById(Long userId);

    User findUserByEmail(String email);

    void createUser(String firstName, String lastName,
                    String email, String password);

    void registryUser(UserRegistryForm userRegistryForm);

    void updateUser(UserUpdateForm userUpdateForm);

    void deleteUser(Long userId);

    List<User> findAllUsers();

    List<User> searchUsersBy(String lastName, String email);

}

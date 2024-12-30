package com.example.jungleroyal.service;

import com.example.jungleroyal.domain.user.UserDto;

public interface UserService {
    String getUsernameById(String userId);

    void updateNickName(UserDto userDto);
}

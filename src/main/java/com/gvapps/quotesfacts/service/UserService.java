package com.gvapps.quotesfacts.service;

import com.gvapps.quotesfacts.dto.UserDTO;
import java.util.List;

public interface UserService {
    UserDTO saveOrUpdate(UserDTO userDTO);
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    void deleteUser(Long id);
    void updateNotificationAndFcmToken(Long id, String appName, Boolean notificationEnabled, String fcmToken);

}

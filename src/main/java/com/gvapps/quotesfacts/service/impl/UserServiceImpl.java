package com.gvapps.quotesfacts.service.impl;

import com.gvapps.quotesfacts.dto.UserDTO;
import com.gvapps.quotesfacts.entity.UserEntity;
import com.gvapps.quotesfacts.mapper.UserMapper;
import com.gvapps.quotesfacts.repository.UserRepository;
import com.gvapps.quotesfacts.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.gvapps.quotesfacts.util.Utils.getJsonString;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO saveOrUpdate(UserDTO userDTO) {
        UserEntity userEntity;
        log.info("[UserServiceImpl] >> [saveOrUpdate] userDTO: {}", getJsonString(userDTO));
        if (userDTO.getId() != null) {
            // 🧩 Update existing user by ID
            userEntity = userRepository.findById(userDTO.getId())
                    .orElse(new UserEntity());
            userMapper.updateEntityFromDto(userDTO, userEntity);
        } else if (userDTO.getDeviceId() != null) {
            // 🆕 Check existing by deviceId to avoid duplicates
            userEntity = userRepository.findByDeviceId(userDTO.getDeviceId())
                    .orElse(userMapper.toEntity(userDTO));
            userMapper.updateEntityFromDto(userDTO, userEntity);
        } else {
            // new user
            log.info("[UserServiceImpl] >> [saveOrUpdate] New user");
            userEntity = userMapper.toEntity(userDTO);
        }

        UserEntity saved = userRepository.save(userEntity);
        return userMapper.toDto(saved);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void updateNotificationAndFcmToken(Long id, String appName, Boolean notificationEnabled, String fcmToken) {
        log.info("[UserServiceImpl] >> [updateNotificationAndFcmToken] id: {}, appName: {}, notificationEnabled: {}, fcmToken: {}",
                id, appName, notificationEnabled, fcmToken);

        int updated = userRepository.updateNotificationAndFcmTokenByIdAndAppName(id, appName, notificationEnabled, fcmToken);

        if (updated == 0) {
            log.warn("[UserServiceImpl] >> [updateNotificationAndFcmToken] No user found with id: {} and appName: {}", id, appName);
            throw new RuntimeException("User not found for id: " + id + " and appName: " + appName);
        }
    }
}

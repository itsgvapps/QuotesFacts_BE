package com.gvapps.quotesfacts.controller;

import com.gvapps.quotesfacts.dto.UserDTO;
import com.gvapps.quotesfacts.dto.request.UpdateNotificationRequest;
import com.gvapps.quotesfacts.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gvapps.quotesfacts.util.Utils.getJsonString;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ✅ Allow access from mobile app or frontend
public class UserController {

    private final UserService userService;

    @PostMapping("/save")
    public ResponseEntity<UserDTO> saveOrUpdate(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.saveOrUpdate(userDTO));
    }

    @PostMapping("/update-notification")
    public ResponseEntity<String> updateNotification(@RequestBody UpdateNotificationRequest request) {
        log.info("[UserController] >> [updateNotification] request: {}", getJsonString(request));
        userService.updateNotificationAndFcmToken(
                request.getId(),
                request.getAppName(),
                request.getNotificationEnabled(),
                request.getFcmToken()
        );
        return ResponseEntity.ok("User notification and FCM token updated successfully.");
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

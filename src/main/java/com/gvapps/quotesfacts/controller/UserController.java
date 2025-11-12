package com.gvapps.quotesfacts.controller;

import com.gvapps.quotesfacts.dto.UserDTO;
import com.gvapps.quotesfacts.dto.request.UpdateNotificationRequest;
import com.gvapps.quotesfacts.dto.response.APIResponse;
import com.gvapps.quotesfacts.service.UserService;
import com.gvapps.quotesfacts.util.ResponseUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.gvapps.quotesfacts.util.Utils.getJsonString;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping("/save")
    public ResponseEntity<APIResponse> saveOrUpdate(@RequestBody UserDTO userDTO) {
        log.info("[UserController] >> [saveOrUpdate] userDTO: {}", getJsonString(userDTO));
        UserDTO savedUser = userService.saveOrUpdate(userDTO);
        return ResponseEntity.ok(ResponseUtils.success("200", "User saved successfully", savedUser));
    }

    @PostMapping("/update-notification")
    public ResponseEntity<APIResponse> updateNotification(@RequestBody UpdateNotificationRequest request) {
        log.info("[UserController] >> [updateNotification] request: {}", getJsonString(request));
        userService.updateNotificationAndFcmToken(
                request.getId(),
                request.getAppName(),
                request.getNotificationEnabled(),
                request.getFcmToken()
        );
        return ResponseEntity.ok(ResponseUtils.success("200", "User notification updated", null));
    }

    @GetMapping
    public ResponseEntity<APIResponse> getAllUsers() {
        return ResponseEntity.ok(ResponseUtils.success("200", "Users retrieved", userService.getAllUsers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        if (user == null) throw new EntityNotFoundException("User not found with id: " + id);
        return ResponseEntity.ok(ResponseUtils.success("200", "User retrieved", user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ResponseUtils.success("200", "User deleted successfully", null));
    }

}

package com.gvapps.quotesfacts.controller;

import com.gvapps.quotesfacts.dto.ImageCollectionDTO;
import com.gvapps.quotesfacts.dto.response.APIResponse;
import com.gvapps.quotesfacts.service.ContentImageSetService;
import com.gvapps.quotesfacts.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gvapps.quotesfacts.util.Constants.SET_IMAGE_TYPE_PSYCHOLOGY;

@Slf4j
@RestController
@RequestMapping("/v1/psychology")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ✅ Allow access from mobile app or frontend
public class PsychologyController {
    private final ContentImageSetService contentImageSetService;

    /* get Images */
    @GetMapping("/images/latest")
    public ResponseEntity<APIResponse> getLatestImages() {
        ImageCollectionDTO result = contentImageSetService.getRandomImages(SET_IMAGE_TYPE_PSYCHOLOGY, 1);
        return ResponseEntity.ok(ResponseUtils.success("200", "Images fetched successfully", result.images(), result.title(), result.subTitle()));
    }

    /* get Images */
    @GetMapping("/images/random")
    public ResponseEntity<APIResponse> getRandomImages() {
        ImageCollectionDTO result = contentImageSetService.getRandomImages(SET_IMAGE_TYPE_PSYCHOLOGY, 1);
        return ResponseEntity.ok(ResponseUtils.success("200", "Images fetched successfully", result.images(), result.title(), result.subTitle()));
    }
}

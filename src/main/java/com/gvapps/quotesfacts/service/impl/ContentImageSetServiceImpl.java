package com.gvapps.quotesfacts.service.impl;

import com.gvapps.quotesfacts.dto.FactImageResponse;
import com.gvapps.quotesfacts.dto.ImageCollectionDTO;
import com.gvapps.quotesfacts.entity.ContentImageSetEntity;
import com.gvapps.quotesfacts.exception.ApiException;
import com.gvapps.quotesfacts.repository.ContentImageSetRepository;
import com.gvapps.quotesfacts.service.ContentImageSetService;
import com.gvapps.quotesfacts.util.Constants;
import com.gvapps.quotesfacts.util.ContentImageSetBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentImageSetServiceImpl implements ContentImageSetService {

    private static final int MAX_RANDOM_ATTEMPTS_MULTIPLIER = 8;

    private final ContentImageSetRepository contentImageSetRepository;

    @Override
    public ImageCollectionDTO getRandomImages(int typeId, int categoryId) {
        ContentImageSetEntity imageSet = contentImageSetRepository.findRandomActiveByTypeIdAndCategoryId(typeId, categoryId)
                .orElseThrow(() -> new ApiException("404", "No active image set found for typeId=" + typeId + ", categoryId=" + categoryId));

        return buildRandomImagesOrThrow(List.of(imageSet), "No images found for image set=" + imageSet.getName());
    }

    @Override
    public ImageCollectionDTO getImagesBySetName(String setName) {
        if (setName == null || setName.isBlank()) {
            throw new ApiException("400", "setName is required");
        }

        ContentImageSetEntity imageSet = contentImageSetRepository.findByNameAndActiveTrue(setName.trim())
                .orElseThrow(() -> new ApiException("404", "No active image set found for setName=" + setName));

        return buildRandomImagesOrThrow(List.of(imageSet), "No images found for setName=" + setName);
    }

    @Override
    public ImageCollectionDTO getImagesByCategoryId(int categoryId) {
        validatePositive(categoryId, "categoryId");

        List<ContentImageSetEntity> imageSets = contentImageSetRepository.findActiveByCategoryId(categoryId);
        return buildRandomImagesOrThrow(imageSets, "No active image sets found for categoryId=" + categoryId);
    }

    @Override
    public ImageCollectionDTO getLatestImages(int typeId, int categoryId) {
        List<ContentImageSetEntity> imageSets = contentImageSetRepository.findTop20ByActiveTrueOrderByUpdatedAtDescCreatedAtDesc();
        return buildRandomImagesOrThrow(imageSets, "No active image sets found");
    }

    @Override
    public ImageCollectionDTO getHomeImages(int typeId, int categoryId) {
        ContentImageSetEntity imageSet = contentImageSetRepository.findRandomActiveByTypeIdAndCategoryId(typeId, categoryId)
                .orElseThrow(() -> new ApiException("404", "No active image set found"));

        return buildRandomImagesOrThrow(
                List.of(imageSet),
                Constants.TAB_IMAGE_SET_IMAGES_LIMIT,
                "No images found for image set=" + imageSet.getName()
        );
    }

    private ImageCollectionDTO buildRandomImagesOrThrow(List<ContentImageSetEntity> imageSets, String emptyMessage) {
        return buildRandomImagesOrThrow(imageSets, Constants.CONTENT_IMAGE_SET_IMAGES_LIMIT, emptyMessage);
    }

    private ImageCollectionDTO buildRandomImagesOrThrow(List<ContentImageSetEntity> imageSets, int limit, String emptyMessage) {
        List<ContentImageSetEntity> validImageSets = imageSets.stream()
                .filter(this::hasValidRange)
                .toList();

        if (validImageSets.isEmpty()) {
            throw new ApiException("404", emptyMessage);
        }

        List<FactImageResponse> images = buildRandomImages(validImageSets, limit);
        if (images.isEmpty()) {
            throw new ApiException("404", emptyMessage);
        }
        ContentImageSetEntity titleSource = validImageSets.get(0);
        return new ImageCollectionDTO(titleSource.getTitle(), titleSource.getSubTitle(), images);
    }

    private List<FactImageResponse> buildRandomImages(List<ContentImageSetEntity> imageSets, int limit) {
        long totalAvailable = imageSets.stream()
                .mapToLong(this::countAvailableImages)
                .sum();
        int safeLimit = (int) Math.min(limit, totalAvailable);

        Set<String> selectedKeys = new LinkedHashSet<>();
        List<FactImageResponse> responses = new ArrayList<>();
        int maxAttempts = Math.max(safeLimit * MAX_RANDOM_ATTEMPTS_MULTIPLIER, safeLimit);

        while (responses.size() < safeLimit && selectedKeys.size() < totalAvailable && maxAttempts-- > 0) {
            ContentImageSetEntity imageSet = imageSets.get(ThreadLocalRandom.current().nextInt(imageSets.size()));
            Long imageId = randomImageId(imageSet);
            String key = imageSet.getImageSetId() + ":" + imageId;

            if (selectedKeys.add(key)) {
                responses.add(ContentImageSetBuilder.build(imageSet, imageId));
            }
        }

        if (responses.size() < safeLimit) {
            fillRemainingSequentially(imageSets, selectedKeys, responses, safeLimit);
        }

        Collections.shuffle(responses);
        return responses;
    }

    private void fillRemainingSequentially(
            List<ContentImageSetEntity> imageSets,
            Set<String> selectedKeys,
            List<FactImageResponse> responses,
            int safeLimit
    ) {
        for (ContentImageSetEntity imageSet : imageSets) {
            for (long imageId = imageSet.getMinId(); imageId <= imageSet.getMaxId() && responses.size() < safeLimit; imageId++) {
                String key = imageSet.getImageSetId() + ":" + imageId;
                if (selectedKeys.add(key)) {
                    responses.add(ContentImageSetBuilder.build(imageSet, imageId));
                }
            }
            if (responses.size() >= safeLimit) {
                return;
            }
        }
    }

    private boolean hasValidRange(ContentImageSetEntity imageSet) {
        return imageSet.getMinId() != null
                && imageSet.getMaxId() != null
                && imageSet.getMaxId() >= imageSet.getMinId();
    }

    private long countAvailableImages(ContentImageSetEntity imageSet) {
        return imageSet.getMaxId() - imageSet.getMinId() + 1;
    }

    private Long randomImageId(ContentImageSetEntity imageSet) {
        return ThreadLocalRandom.current().nextLong(imageSet.getMinId(), imageSet.getMaxId() + 1);
    }

    private void validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new ApiException("400", fieldName + " must be greater than zero");
        }
    }
}

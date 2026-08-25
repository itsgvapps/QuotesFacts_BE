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

    /*
     * Maximum number of image sets loaded for a category request.
     *
     * This prevents a category containing thousands of active image
     * sets from causing an unnecessarily large Hibernate result set.
     *
     * This does NOT limit the number of images returned to the client.
     * The existing Constants.IMAGES_LIMIT continues to control that.
     */
    private static final int MAX_IMAGE_SETS_PER_CATEGORY = 20;

    private final ContentImageSetRepository contentImageSetRepository;

    @Override
    public ImageCollectionDTO getRandomImages(int typeId, int categoryId) {
        ContentImageSetEntity imageSet = contentImageSetRepository.findRandomActiveByTypeIdAndCategoryId(typeId, categoryId).orElseThrow(() -> new ApiException("404", "No active image set found for typeId=" + typeId + ", categoryId=" + categoryId));

        return buildRandomImagesOrThrow(List.of(imageSet), Constants.VISUAL_IMAGES_LIMIT, "No images found for image set=" + imageSet.getName());
    }

    @Override
    public ImageCollectionDTO getLatestImages(int typeId, int categoryId) {
        ContentImageSetEntity imageSet = contentImageSetRepository.findRandomActiveByTypeIdAndCategoryId(typeId, categoryId).orElseThrow(() -> new ApiException("404", "No active image set found for typeId=" + typeId + ", categoryId=" + categoryId));

        return buildRandomImagesOrThrow(List.of(imageSet), Constants.IMAGES_LIMIT, "No images found for image set=" + imageSet.getName());
    }

    @Override
    public ImageCollectionDTO getImagesBySetName(String setName) {
        if (setName == null || setName.isBlank()) {
            throw new ApiException("400", "setName is required");
        }

        String normalizedSetName = setName.trim();

        ContentImageSetEntity imageSet = contentImageSetRepository.findByNameAndActiveTrue(normalizedSetName).orElseThrow(() -> new ApiException("404", "No active image set found for setName=" + normalizedSetName));

        return buildRandomImagesOrThrow(List.of(imageSet), Constants.IMAGES_LIMIT, "No images found for setName=" + normalizedSetName);
    }

    @Override
    public ImageCollectionDTO getImagesByCategoryId(int categoryId) {
        validatePositive(categoryId, "categoryId");

        /*
         * IMPORTANT:
         *
         * The repository query itself is now bounded.
         *
         * This prevents thousands of ContentImageSetEntity objects
         * from being loaded into the JVM.
         */
        List<ContentImageSetEntity> imageSets = contentImageSetRepository.findActiveByCategoryId(categoryId, MAX_IMAGE_SETS_PER_CATEGORY);

        return buildRandomImagesOrThrow(imageSets, Constants.IMAGES_LIMIT, "No active image sets found for categoryId=" + categoryId);
    }

    @Override
    public ImageCollectionDTO getHomeImages(int typeId, int categoryId) {
        ContentImageSetEntity imageSet = contentImageSetRepository.findRandomActiveByTypeIdAndCategoryId(typeId, categoryId).orElseThrow(() -> new ApiException("404", "No active image set found"));

        return buildRandomImagesOrThrow(List.of(imageSet), Constants.TAB_IMAGE_SET_IMAGES_LIMIT, "No images found for image set=" + imageSet.getName());
    }

    /**
     * Validates image sets and creates only the requested number
     * of image responses.
     * <p>
     * Memory characteristics:
     * <p>
     * O(number of image sets + requested image limit)
     * <p>
     * It never creates a FactImageResponse for every possible
     * image ID in an image-set range.
     */
    private ImageCollectionDTO buildRandomImagesOrThrow(List<ContentImageSetEntity> imageSets, int limit, String emptyMessage) {
        if (imageSets == null || imageSets.isEmpty()) {
            throw new ApiException("404", emptyMessage);
        }

        if (limit <= 0) {
            throw new ApiException("404", emptyMessage);
        }

        List<ContentImageSetEntity> validImageSets = imageSets.stream().filter(Objects::nonNull).filter(this::hasValidRange).toList();

        if (validImageSets.isEmpty()) {
            throw new ApiException("404", emptyMessage);
        }

        List<FactImageResponse> images = buildRandomImages(validImageSets, limit);

        if (images.isEmpty()) {
            throw new ApiException("404", emptyMessage);
        }

        /*
         * Preserve the existing behavior:
         * title/subtitle are taken from the first valid image set.
         */
        ContentImageSetEntity titleSource = validImageSets.get(0);

        return new ImageCollectionDTO(titleSource.getTitle(), titleSource.getSubTitle(), images);
    }

    /**
     * Builds random image responses without materializing the
     * complete image ID range.
     * <p>
     * Example:
     * <p>
     * minId       = 1
     * maxId       = 1,000,000
     * limit       = 20
     * <p>
     * Only approximately 20 FactImageResponse objects are created.
     * <p>
     * The method does NOT create one million objects.
     */
    private List<FactImageResponse> buildRandomImages(List<ContentImageSetEntity> imageSets, int limit) {
        if (imageSets == null || imageSets.isEmpty() || limit <= 0) {
            return Collections.emptyList();
        }

        long totalAvailable = calculateTotalAvailable(imageSets);

        if (totalAvailable <= 0L) {
            return Collections.emptyList();
        }

        int safeLimit = (int) Math.min((long) limit, totalAvailable);

        /*
         * Pre-size the ArrayList.
         *
         * Prevents repeated ArrayList resizing.
         */
        List<FactImageResponse> responses = new ArrayList<>(safeLimit);

        /*
         * Keep selected IDs separately for each image set.
         *
         * Example:
         *
         * imageSet 10 -> [100, 250, 400]
         * imageSet 20 -> [50, 700]
         *
         * This is more memory efficient than constructing String
         * keys such as:
         *
         *     "10:100"
         *     "10:250"
         */
        Map<Long, Set<Long>> selectedIdsByImageSet = new HashMap<>();

        ThreadLocalRandom random = ThreadLocalRandom.current();

        /*
         * Use long to prevent integer overflow.
         */
        long calculatedAttempts = (long) safeLimit * MAX_RANDOM_ATTEMPTS_MULTIPLIER;

        long maxAttempts = Math.max((long) safeLimit, calculatedAttempts);

        /*
         * Random selection.
         */
        while (responses.size() < safeLimit && maxAttempts-- > 0L) {

            ContentImageSetEntity imageSet = imageSets.get(random.nextInt(imageSets.size()));

            long imageId = randomImageId(imageSet);

            Long imageSetId = imageSet.getImageSetId();

            Set<Long> selectedIds = selectedIdsByImageSet.computeIfAbsent(imageSetId, key -> new HashSet<>());

            /*
             * Only create the response when the image hasn't
             * already been selected from this image set.
             */
            if (selectedIds.add(imageId)) {

                responses.add(ContentImageSetBuilder.build(imageSet, imageId));
            }
        }

        /*
         * Random sampling becomes inefficient when the requested
         * number approaches the total number of available images.
         *
         * Fill the remaining slots sequentially.
         */
        if (responses.size() < safeLimit) {

            fillRemainingSequentially(imageSets, selectedIdsByImageSet, responses, safeLimit);
        }

        /*
         * Preserve existing behavior.
         */
        if (responses.size() > 1) {
            Collections.shuffle(responses, random);
        }

        return responses;
    }

    /**
     * Fills missing images without constructing an intermediate
     * collection of the entire image range.
     * <p>
     * The loop stops immediately when the requested limit is reached.
     */
    private void fillRemainingSequentially(List<ContentImageSetEntity> imageSets, Map<Long, Set<Long>> selectedIdsByImageSet, List<FactImageResponse> responses, int safeLimit) {
        for (ContentImageSetEntity imageSet : imageSets) {

            if (responses.size() >= safeLimit) {
                return;
            }

            Long imageSetId = imageSet.getImageSetId();

            Set<Long> selectedIds = selectedIdsByImageSet.computeIfAbsent(imageSetId, key -> new HashSet<>());

            long minId = imageSet.getMinId();

            long maxId = imageSet.getMaxId();

            /*
             * Overflow-safe iteration.
             *
             * We intentionally do NOT use:
             *
             *     imageId <= maxId
             *
             * with an unconditional imageId++.
             *
             * If maxId == Long.MAX_VALUE, imageId++ would overflow.
             */
            for (long imageId = minId; ; imageId++) {

                if (selectedIds.add(imageId)) {

                    responses.add(ContentImageSetBuilder.build(imageSet, imageId));

                    if (responses.size() >= safeLimit) {
                        return;
                    }
                }

                if (imageId == maxId) {
                    break;
                }
            }
        }
    }

    /**
     * Calculates the total number of available image IDs.
     * <p>
     * Uses overflow-safe arithmetic.
     */
    private long calculateTotalAvailable(List<ContentImageSetEntity> imageSets) {
        long total = 0L;

        for (ContentImageSetEntity imageSet : imageSets) {

            long available = countAvailableImages(imageSet);

            /*
             * Saturate rather than overflow.
             */
            if (Long.MAX_VALUE - total < available) {
                return Long.MAX_VALUE;
            }

            total += available;
        }

        return total;
    }

    /**
     * Validates an image set.
     */
    private boolean hasValidRange(ContentImageSetEntity imageSet) {
        return imageSet != null && imageSet.getImageSetId() != null && imageSet.getMinId() != null && imageSet.getMaxId() != null && imageSet.getMaxId() >= imageSet.getMinId();
    }

    /**
     * Counts the inclusive range:
     * <p>
     * minId ... maxId
     * <p>
     * Example:
     * <p>
     * 10 ... 19 = 10 images
     */
    private long countAvailableImages(ContentImageSetEntity imageSet) {
        long minId = imageSet.getMinId();

        long maxId = imageSet.getMaxId();

        if (maxId < minId) {
            return 0L;
        }

        long difference = maxId - minId;

        /*
         * Prevent:
         *
         * Long.MAX_VALUE + 1
         *
         * from overflowing.
         */
        if (difference == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }

        return difference + 1L;
    }

    /**
     * Returns a random image ID from the inclusive range:
     * <p>
     * [minId, maxId]
     * <p>
     * For normal database image IDs, maxId will be far below
     * Long.MAX_VALUE, so the first branch is used.
     */
    private long randomImageId(ContentImageSetEntity imageSet) {
        long minId = imageSet.getMinId();

        long maxId = imageSet.getMaxId();

        /*
         * Normal case.
         *
         * nextLong(origin, bound) uses an exclusive bound.
         */
        if (maxId < Long.MAX_VALUE) {

            return ThreadLocalRandom.current().nextLong(minId, maxId + 1L);
        }

        /*
         * Extremely defensive case:
         * the range contains only Long.MAX_VALUE.
         */
        if (minId == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }

        /*
         * For real-world image IDs this branch should effectively
         * never be needed. Rejection sampling avoids overflow.
         */
        long candidate;

        do {
            candidate = ThreadLocalRandom.current().nextLong();
        } while (candidate < minId);

        return candidate;
    }

    private void validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new ApiException("400", fieldName + " must be greater than zero");
        }
    }
}
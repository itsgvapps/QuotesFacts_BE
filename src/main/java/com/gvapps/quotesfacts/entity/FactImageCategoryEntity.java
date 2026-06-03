package com.gvapps.quotesfacts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fact_image_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactImageCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_category_id")
    private Long imageCategoryId;

    private String name;

    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "category_type")
    private String categoryType;

    @Column(name = "base_folder")
    private String baseFolder;

    @Column(name = "image_prefix")
    private String imagePrefix;

    @Column(name = "min_id")
    private Long minId;

    @Column(name = "max_id")
    private Long maxId;

    @Column(name = "image_extension")
    private String imageExtension;

    @Column(name = "small_image_folder")
    private String smallImageFolder;

    @Column(name = "medium_image_folder")
    private String mediumImageFolder;

    @Column(name = "large_image_folder")
    private String largeImageFolder;

    @Column(name = "original_image_folder")
    private String originalImageFolder;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "author_url")
    private String authorUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description")
    private String shortDescription;

    private String icon;

    @Column(name = "square_image")
    private String squareImage;

    @Column(name = "preview_image")
    private String previewImage;

    @Column(name = "vertical_image")
    private String verticalImage;

    @Column(name = "vertical_image_with_text")
    private String verticalImageWithText;

    @Column(name = "horizontal_image")
    private String horizontalImage;

    @Column(name = "horizontal_image_with_text")
    private String horizontalImageWithText;

    @Column(columnDefinition = "JSON")
    private String tags;

    @Column(columnDefinition = "JSON")
    private String metadata;

    private String type;

    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "language_code")
    private String languageCode;

    private boolean featured;
    private boolean active;
    private int views;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}


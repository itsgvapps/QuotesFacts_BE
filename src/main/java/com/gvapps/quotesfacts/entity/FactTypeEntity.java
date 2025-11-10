package com.gvapps.quotesfacts.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "FACT_TYPE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactTypeEntity {

    @Id
    @Column(name = "category_id")
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "short_description", length = 255)
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "type_id")
    private int typeId;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean active = true;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean featured = false;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int views;

    @Column(name = "tags", columnDefinition = "JSON")
    private String tags;

    private String icon;
    private String image;
    private String largeImage;
    private String previewImage;
    private String verticalImage;
    private String verticalImageWithText;
    private String horizontalImage;
    private String horizontalImageWithText;
    private String externalUrl;

    @Column(name = "added_date")
    private LocalDate addedDate;

    @Column(name = "updated_date")
    private LocalDate updatedDate;
}

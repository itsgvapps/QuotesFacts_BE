package com.gvapps.quotesfacts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "fact_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // Foreign key to FACT_TYPE
    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(name = "short_summary", length = 255)
    private String shortSummary;

    @Column(name = "long_summary", columnDefinition = "TEXT")
    private String longSummary;

    @Column(name = "article_id", columnDefinition = "INT DEFAULT 0")
    private int articleId = 0;

    // Store as JSON string (can later use @Type(JsonType.class) if using Hibernate Types)
    @Column(name = "source_url", columnDefinition = "JSON")
    private String sourceUrl;

    @Column(name = "likes", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int likes = 0;

    @Column(name = "bookmarks", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int bookmarks = 0;

    @Column(name = "downloads", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int downloads = 0;

    @Column(name = "shares", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int shares = 0;

    @Column(name = "views", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int views = 0;

    @Column(name = "tags", columnDefinition = "JSON")
    private String tags;

    @Column(name = "external_url", length = 255)
    private String externalUrl;

    @Column(name = "verified", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean verified = false;

    @Column(name = "language", length = 10, columnDefinition = "VARCHAR(10) DEFAULT 'en'")
    private String language = "en";

    @Column(name = "added_date")
    private LocalDate addedDate;

    @Column(name = "updated_date")
    private LocalDate updatedDate;
}

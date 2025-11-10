package com.gvapps.quotesfacts.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "articles",
        indexes = {
                @Index(name = "idx_category_id", columnList = "category_id"),
                @Index(name = "idx_type", columnList = "type"),
                @Index(name = "idx_active", columnList = "active"),
                @Index(name = "idx_featured", columnList = "featured")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticlesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "sub_title")
    private String subTitle;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private String source;
    private String summary;
    private String author;

    @Column(name = "img_credit")
    private String imgCredit;

    @Column(name = "img_path")
    private String imgPath;

    @Column(name = "external_url")
    private String externalUrl;

    private String type;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Type(JsonType.class)
    @Column(name = "tags", columnDefinition = "JSON")
    private List<String> tags;

    @Column(name = "added_date")
    private LocalDate addedDate;

    @Column(name = "updated_date")
    private LocalDate updatedDate;

    private String header;

    private boolean active;
    private boolean featured;

    private int likes;
    private int views;
    private int downloads;
    private int favourites;
    private int bookmarks;

    @Column(name = "reading_time")
    private int readingTime;

    private String notes;
}

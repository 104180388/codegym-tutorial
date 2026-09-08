package com.example.case_study_2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsDto {

    private Long id;
    private String url;
    private String source;
    private String title;
    private String summary;

    @JsonProperty("published_time")
    private String publishedTime;

    private String author;

    @JsonProperty("cover_image")
    private String coverImage;

    private List<NewsImageDto> images = new ArrayList<>();
    private String content;
    private List<String> paragraphs = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    @JsonProperty("scraped_at")
    private String scrapedAt;

    public NewsDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(String publishedTime) {
        this.publishedTime = publishedTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<NewsImageDto> getImages() {
        return images;
    }

    public void setImages(List<NewsImageDto> images) {
        this.images = images != null ? images : new ArrayList<>();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<String> paragraphs) {
        this.paragraphs = paragraphs != null ? paragraphs : new ArrayList<>();
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public String getScrapedAt() {
        return scrapedAt;
    }

    public void setScrapedAt(String scrapedAt) {
        this.scrapedAt = scrapedAt;
    }
}

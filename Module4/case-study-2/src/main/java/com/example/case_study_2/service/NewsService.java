package com.example.case_study_2.service;

import com.example.case_study_2.dto.NewsDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final List<NewsDto> newsList = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        loadNewsFromJson();
    }

    public synchronized void loadNewsFromJson() {
        try {
            ClassPathResource resource = new ClassPathResource("data/news.json");
            if (resource.exists()) {
                try (InputStream inputStream = resource.getInputStream()) {
                    List<NewsDto> list = objectMapper.readValue(inputStream, new TypeReference<List<NewsDto>>() {});
                    newsList.clear();
                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            NewsDto item = list.get(i);
                            if (item.getId() == null) {
                                item.setId((long) (i + 1));
                            }
                            newsList.add(item);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi đọc file news.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<NewsDto> getAllNews() {
        return Collections.unmodifiableList(newsList);
    }

    public Optional<NewsDto> getNewsById(Long id) {
        if (id == null) return Optional.empty();
        return newsList.stream()
                .filter(n -> id.equals(n.getId()))
                .findFirst();
    }

    public List<NewsDto> getLatestNews(int count) {
        return newsList.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<NewsDto> getFeaturedNews(int skip, int count) {
        return newsList.stream()
                .skip(skip)
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<NewsDto> getRelatedNews(Long currentId, int count) {
        return newsList.stream()
                .filter(n -> currentId == null || !currentId.equals(n.getId()))
                .limit(count)
                .collect(Collectors.toList());
    }
}

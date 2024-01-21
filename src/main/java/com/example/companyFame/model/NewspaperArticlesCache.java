package com.example.companyFame.model;

import com.example.companyFame.utils.IArticleFilesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewspaperArticlesCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewspaperArticlesCache.class);

    private final List<String> newspaperArticles;

    public NewspaperArticlesCache(IArticleFilesReader articleFilesReader) {
        this.newspaperArticles = new ArrayList<>();

        LOGGER.info("Starting to download articles");
        try {
            articleFilesReader.parseAllFilesFromTheFolder(this.newspaperArticles);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't parse articles");
        }
        LOGGER.info("Finished downloading articles");
    }

    public List<String> getNewspaperArticles() {
        return newspaperArticles;
    }
}

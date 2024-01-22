package com.example.companyFame.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class CsvReaderImplArticleFilesReader implements IArticleFilesReader {

    @Value("${articles.files.path}")
    String pathToArticles;

    public CsvReaderImplArticleFilesReader(String pathToArticles) {
        this.pathToArticles = pathToArticles;
    }

    /**
     * write articles from the resource files to the provided list
     *
     * @param articles  list of articles that were extracted from the resource files
     */
    @Override
    public void parseAllFilesFromTheFolder(List<String> articles) throws IOException {
        List<String> articlesNames = getAllFilesFromResourceFolder(pathToArticles);
        for (String articleName : articlesNames) {
            String filePath = pathToArticles + articleName;
            InputStream in = getClass().getClassLoader().getResourceAsStream(filePath);
            try (Reader reader = new InputStreamReader(in, UTF_8)) {
                articles.add(FileCopyUtils.copyToString(reader));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private List<String> getAllFilesFromResourceFolder(String pathToArticles) throws IOException {
        List<String> articlesNames = new ArrayList<>();

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(pathToArticles);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;
            while ((resource = br.readLine()) != null) {
                articlesNames.add(resource);
            }
        }
        return articlesNames;
    }

}

package com.example.companyFame.utils;

import java.io.IOException;
import java.util.List;

public interface IArticleFilesReader {

    /**
     * write articles from the resource files to the provided list
     * @param articles          list of articles that were extracted from the resource files
     */
    void parseAllFilesFromTheFolder(List<String> articles) throws IOException;
}

package com.example.companyFame.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReaderImplArticleFilesReaderTest {

    private IArticleFilesReader sut;

    @Test
    void parseAllFilesFromTheFolder_getContextOfArticles_whenFilesDataIsValid() throws IOException {
        //given
        String filePath = "testData/data/";
        sut = new CsvReaderImplArticleFilesReader(filePath);
        List<String> articles = new ArrayList<>();

        //when
        sut.parseAllFilesFromTheFolder(articles);

        //then
        Assertions.assertEquals(11, articles.size());
    }

}

package com.example.companyFame.service;

import com.example.companyFame.model.Company;
import com.example.companyFame.model.CompanyCsv;
import com.example.companyFame.model.CompanyInfoCache;
import com.example.companyFame.model.NewspaperArticlesCache;
import com.example.companyFame.utils.ArticleFilesReader;
import com.example.companyFame.utils.CompanyListParser;
import com.example.companyFame.utils.IArticleFilesReader;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchersCountServiceTest {

    private final List<String> articles = new ArrayList<>();
    private final Map<Integer, CompanyCsv> companiesGeneralInfo = new HashMap<>();
    private final Map<Integer, Company> companiesRelatedNames = new HashMap<>();

    @Mock
    private CompanyInfoCache companyInfoCache;
    @Mock
    private NewspaperArticlesCache newspaperArticlesCache;

    @InjectMocks
    private MatchersCountService sut;

    @BeforeEach
    void warmUp() throws IOException, CsvException {
        String pathToArticles = "testData/data/";
        IArticleFilesReader xmlFileReader = new ArticleFilesReader(pathToArticles);
        xmlFileReader.parseAllFilesFromTheFolder(articles);

        String pathToCompaniesCsv = "testData/1_company_list.csv";
        CompanyListParser csvParser = new CompanyListParser(pathToCompaniesCsv);
        csvParser.parseCsvToCompaniesInfos(companiesGeneralInfo, companiesRelatedNames);
    }

    @Test
    void calculateNumberOfCompanyNameMentionsInAllArticles_returnNumberOfCompanyNameAndRefsMentions_whenCompanyIsMentionedInOneArticle() {
        //given
        int companyId = 1;
        Company company = companiesRelatedNames.get(companyId);

        when(companyInfoCache.getCompanyById(companyId)).thenReturn(companiesGeneralInfo.get(companyId));

        //when
        int actualNumberOfMentions = sut.calculateNumberOfCompanyNameMentionsInAllArticles(companyId, company.possibleNames(), articles);

        //then
        Assertions.assertEquals(12, actualNumberOfMentions);
    }

    @Test
    void calculateNumberOfCompanyNameMentionsInAllArticles_returnNumberOfCompanyNameAndRefsMentions_whenCompanyIsMentionedInFewArticles() {
        //given
        int companyId = 4;
        Company company = companiesRelatedNames.get(companyId);

        when(companyInfoCache.getCompanyById(companyId)).thenReturn(companiesGeneralInfo.get(companyId));

        //when
        int actualNumberOfMentions = sut.calculateNumberOfCompanyNameMentionsInAllArticles(companyId, company.possibleNames(), articles);

        //then
        Assertions.assertEquals(30, actualNumberOfMentions);
    }

    @Test
    void calculateNumberOfCompanyNameMentionsInAllArticles_returnNumberOfCompanyNameAndRefsMentions_whenCompanyIsNotMentionedInSomeArticles() {
        //given
        int companyId = 16;
        Company company = companiesRelatedNames.get(companyId);

        //when
        int actualNumberOfMentions = sut.calculateNumberOfCompanyNameMentionsInAllArticles(companyId, company.possibleNames(), articles);

        //then
        Assertions.assertEquals(0, actualNumberOfMentions);
    }


}

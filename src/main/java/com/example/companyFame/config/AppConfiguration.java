package com.example.companyFame.config;

import com.example.companyFame.model.CompanyInfoCache;
import com.example.companyFame.model.NewspaperArticlesCache;
import com.example.companyFame.utils.IArticleFilesReader;
import com.example.companyFame.utils.ICompanyListParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public CompanyInfoCache companyInfoCache(ICompanyListParser companyListParser) {
        return new CompanyInfoCache(companyListParser);
    }

    @Bean
    public NewspaperArticlesCache newspaperArticlesCache(IArticleFilesReader articleFilesReader) {
        return new NewspaperArticlesCache(articleFilesReader);
    }

}

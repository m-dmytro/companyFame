package com.example.companyFame.model;

import com.example.companyFame.utils.ICompanyListParser;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CompanyInfoCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyInfoCache.class);

    private final Map<Integer, CompanyCsv> companiesGeneralInfo;
    private final Map<Integer, Company> companiesRelatedNames;

    public CompanyInfoCache(ICompanyListParser companyListParser) {
        this.companiesGeneralInfo = new ConcurrentHashMap<>();
        this.companiesRelatedNames = new ConcurrentHashMap<>();

        LOGGER.info("Starting to download company names from csv file");
        try {
            companyListParser.parseCsvToCompaniesInfos(this.companiesGeneralInfo, this.companiesRelatedNames);
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Invalid CSV File");
        }
        LOGGER.info("Finished downloading company names from csv file");
    }

    public Map<Integer, Company> getCompaniesRelatedNames() {
        return new ConcurrentHashMap<>(this.companiesRelatedNames);
    }

    public CompanyCsv getCompanyById(int id) {
        return companiesGeneralInfo.get(id);
    }
}

package com.example.companyFame.utils;

import com.example.companyFame.model.Company;
import com.example.companyFame.model.CompanyCsv;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.Map;

public interface ICompanyListParser {

    /**
     * parse company csv file, find all possible companies names without duplicates, write them into provided maps
     *
     * @param companiesGeneralInfo      map of companies: key -> company id, values -> company name from the CSV file
     * @param companiesRelatedNames     map of companies: key -> company id, values -> all possible company names
     */
    void parseCsvToCompaniesInfos(Map<Integer, CompanyCsv> companiesGeneralInfo, Map<Integer, Company> companiesRelatedNames) throws IOException, CsvException;

}

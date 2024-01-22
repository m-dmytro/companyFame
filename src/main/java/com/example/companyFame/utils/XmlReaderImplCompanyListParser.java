package com.example.companyFame.utils;

import com.example.companyFame.model.Company;
import com.example.companyFame.model.CompanyCsv;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class XmlReaderImplCompanyListParser implements ICompanyListParser {

    private static final String REFER_TO_TXT = "refer to";
    private static final String KNOWN_AS_TXT = "known as";
    private static final String FORMERLY_TXT = "formerly";
    private static final String EITHER_TXT = "either ";
    private static final String OR_TXT = " or ";
    private static final String AND_TXT = " and ";
    private static final String CSV_DELIMETER = ";";

    private static final String quotesDisctractorsRegExp = "(.+?)\\s*(\\([^)]+\\))?(?:;|$)";
    private static final String referenceDisctractorsRegExp = "(?i)(refer to|known as|formerly)";
    private static final String eitherOrAndDisctractorsRegExp = "(?i)(either | or | and )";

    @Value("${company.file.path}")
    String filePath;

    public XmlReaderImplCompanyListParser(String filePath) {
        this.filePath = filePath;
    }

    /**
     * parse company csv file, find all possible companies names without duplicates, write them into provided maps
     *
     * @param companiesGeneralInfo      map of companies: key -> company id, values -> company name from the CSV file
     * @param companiesRelatedNames     map of companies: key -> company id, values -> all possible company names
     */
    @Override
    public void parseCsvToCompaniesInfos(Map<Integer, CompanyCsv> companiesGeneralInfo, Map<Integer, Company> companiesRelatedNames) throws IOException, CsvException {
        Map<Integer, CompanyCsv> allCompaniesGeneralInfo = new ConcurrentHashMap<>();
        Map<Integer, Company> allCompaniesRelatedNames = new ConcurrentHashMap<>();
        Map<String, Integer> companiesNamesDuplicates = new ConcurrentHashMap<>();

        /* parse csv and find all possible companies names */
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
        try(CSVReader reader = new CSVReaderBuilder(streamReader).withCSVParser(csvParser).withSkipLines(1).build()){
            String[] data;
            while ((data = reader.readNext()) != null) {
                if (data.length == 2) {
                    allCompaniesRelatedNames.put(Integer.parseInt(data[0]), new Company(Integer.parseInt(data[0]), getRelatedNames(data[1], companiesNamesDuplicates)));
                    allCompaniesGeneralInfo.put(Integer.parseInt(data[0]), new CompanyCsv(Integer.parseInt(data[0]), data[1]));
                } else {
                    throw new CsvException("Invalid CSV data: " + String.join(";", data));
                }
            }
        }

        /* remove duplicates in companies names */
        companiesNamesDuplicates.forEach((key1, value1) -> {
            if (value1 > 1) {
                allCompaniesRelatedNames.forEach((key, value) -> value.possibleNames().remove(key1));
            }
        });

        companiesGeneralInfo.putAll(allCompaniesGeneralInfo);
        companiesRelatedNames.putAll(allCompaniesRelatedNames);
    }

    /**
     * parse company csv name and get other possible names
     * Motorola Inc (please refer to either Motorola Solutions Inc or Motorola Mobility Inc) -> Motorola Inc, Motorola Solutions Inc, Motorola Mobility Inc
     */
    private static List<String> getRelatedNames(String name, Map<String, Integer> companiesNamesDuplicates) {
        Pattern pattern = Pattern.compile(quotesDisctractorsRegExp);
        Matcher matcher = pattern.matcher(name);
        List<String> companyNames = new ArrayList<>();

        while (matcher.find()) {
            String mainName = matcher.group(1).trim();
            String additionalNames = matcher.group(2);

            if (additionalNames != null) {
                processPossibleCompanyNamesHighlightedByDistractors(companiesNamesDuplicates, additionalNames, companyNames);
            }
            addCompanyNameToResultList(mainName, companyNames, companiesNamesDuplicates);
        }

        return companyNames;
    }

    private static void processPossibleCompanyNamesHighlightedByDistractors(Map<String, Integer> companiesNamesDuplicates, String additionalNames, List<String> companyNames) {
        additionalNames = additionalNames.substring(1, additionalNames.length() - 1);

        for (String splittedName : Arrays.asList(additionalNames.split(CSV_DELIMETER))) {
            if (isAnotherNamingDescribed(splittedName)) {
                extractPossibleReferenceNames(companiesNamesDuplicates, companyNames, splittedName);
            } else {
                addCompanyNameToResultList(splittedName, companyNames, companiesNamesDuplicates);
            }
        }
    }

    private static void extractPossibleReferenceNames(Map<String, Integer> companiesNamesDuplicates, List<String> companyNames, String splittedName) {
        String[] parts = splittedName.split(referenceDisctractorsRegExp);
        String referName = parts[parts.length-1].trim();

        if (isSeveralPossibleNamesDescribed(referName)) {
            String[] referNameParts = referName.split(eitherOrAndDisctractorsRegExp);
            for (String referNamePart : referNameParts) {
                addCompanyNameToResultList(referNamePart, companyNames, companiesNamesDuplicates);
            }
        } else {
            addCompanyNameToResultList(referName, companyNames, companiesNamesDuplicates);
        }
    }

    private static boolean isAnotherNamingDescribed(String splittedName) {
        return splittedName.contains(REFER_TO_TXT) || splittedName.contains(KNOWN_AS_TXT) || splittedName.contains(FORMERLY_TXT);
    }

    private static boolean isSeveralPossibleNamesDescribed(String referName) {
        return (referName.contains(EITHER_TXT) && referName.contains(OR_TXT)) || referName.contains(AND_TXT);
    }

    private static void addCompanyNameToResultList(String companyName, List<String> companyNames, Map<String, Integer> companiesNamesDuplicates) {
        if (!companyName.isBlank()) {
            companyNames.add(companyName.trim());
            companiesNamesDuplicates.compute(companyName, (key, value) -> value == null ? 1 : value + 1);
        }
    }

}

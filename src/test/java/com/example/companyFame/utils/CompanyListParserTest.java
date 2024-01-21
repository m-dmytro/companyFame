package com.example.companyFame.utils;

import com.example.companyFame.model.Company;
import com.example.companyFame.model.CompanyCsv;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CompanyListParserTest {

    private ICompanyListParser sut;

    @Test
    void parseCsvToCompaniesInfos_splitCompanyNameOntoPossibleRefs_whenFileDataIsValid() throws IOException, CsvException {
        //given
        String filePath = "testData/1_company_list.csv";
        sut = new CompanyListParser(filePath);

        Map<Integer, CompanyCsv> companiesGeneralInfo = new HashMap<>();
        Map<Integer, Company > companiesRelatedNames = new HashMap<>();

        //when
        sut.parseCsvToCompaniesInfos(companiesGeneralInfo, companiesRelatedNames);

        //then
        Assertions.assertEquals(16, companiesGeneralInfo.size());
        Assertions.assertEquals(16, companiesRelatedNames.size());

        Assertions.assertEquals("YUI Holdings Y.U. (formerly known as YUI AMRO Holding Y.U.)", companiesGeneralInfo.get(1).name());
        Assertions.assertTrue(companiesRelatedNames.get(1).possibleNames().contains("YUI Holdings Y.U."));
        Assertions.assertTrue(companiesRelatedNames.get(1).possibleNames().contains("YUI AMRO Holding Y.U."));

        Assertions.assertEquals("Tirigroup Inc (Tiri; Tirigroup)", companiesGeneralInfo.get(2).name());
        Assertions.assertTrue(companiesRelatedNames.get(2).possibleNames().contains("Tirigroup Inc"));
        Assertions.assertTrue(companiesRelatedNames.get(2).possibleNames().contains("Tiri"));
        Assertions.assertTrue(companiesRelatedNames.get(2).possibleNames().contains("Tirigroup"));

        Assertions.assertEquals("Vola-vola Co; The", companiesGeneralInfo.get(3).name());
        Assertions.assertTrue(companiesRelatedNames.get(3).possibleNames().contains("Vola-vola Co"));

        Assertions.assertEquals("Yew Chemical Company; The (Yew)", companiesGeneralInfo.get(6).name());
        Assertions.assertTrue(companiesRelatedNames.get(6).possibleNames().contains("Yew Chemical Company"));
        Assertions.assertTrue(companiesRelatedNames.get(6).possibleNames().contains("Yew"));

        Assertions.assertEquals("Redsoul IOP SA (Please refer to Redsoul SA and IOP SA)", companiesGeneralInfo.get(11).name());
        Assertions.assertTrue(companiesRelatedNames.get(11).possibleNames().contains("Redsoul IOP SA"));
        Assertions.assertTrue(companiesRelatedNames.get(11).possibleNames().contains("Redsoul SA"));
        Assertions.assertTrue(companiesRelatedNames.get(11).possibleNames().contains("IOP SA"));

        Assertions.assertEquals("Motocross Inc (please refer to either Motocross Solutions Inc or Motocross Mobility Inc)", companiesGeneralInfo.get(12).name());
        Assertions.assertTrue(companiesRelatedNames.get(12).possibleNames().contains("Motocross Inc"));
        Assertions.assertTrue(companiesRelatedNames.get(12).possibleNames().contains("Motocross Solutions Inc"));
        Assertions.assertTrue(companiesRelatedNames.get(12).possibleNames().contains("Motocross Mobility Inc"));
    }

    @Test
    void parseCsvToCompaniesInfos_skipDuplicatesInCompanyNames_whenSomeCompaniesContainsSameRefs() throws IOException, CsvException {
        //given
        String filePath = "testData/1_company_list.csv";
        sut = new CompanyListParser(filePath);

        Map<Integer, CompanyCsv> companiesGeneralInfo = new HashMap<>();
        Map<Integer, Company > companiesRelatedNames = new HashMap<>();

        //when
        sut.parseCsvToCompaniesInfos(companiesGeneralInfo, companiesRelatedNames);

        //then
        Assertions.assertEquals(16, companiesGeneralInfo.size());
        Assertions.assertEquals(16, companiesRelatedNames.size());

        Assertions.assertEquals("Vola-vola Co; The", companiesGeneralInfo.get(3).name());
        Assertions.assertEquals(1, companiesRelatedNames.get(3).possibleNames().size());
        Assertions.assertTrue(companiesRelatedNames.get(3).possibleNames().contains("Vola-vola Co"));

        Assertions.assertEquals("Walt Energy Co; The", companiesGeneralInfo.get(5).name());
        Assertions.assertEquals(1, companiesRelatedNames.get(5).possibleNames().size());
        Assertions.assertTrue(companiesRelatedNames.get(5).possibleNames().contains("Walt Energy Co"));

        Assertions.assertEquals("Chakra; PT", companiesGeneralInfo.get(15).name());
        Assertions.assertEquals(1, companiesRelatedNames.get(15).possibleNames().size());
        Assertions.assertTrue(companiesRelatedNames.get(15).possibleNames().contains("Chakra"));

        Assertions.assertEquals("Baunti Agro Firma; PT", companiesGeneralInfo.get(16).name());
        Assertions.assertEquals(1, companiesRelatedNames.get(16).possibleNames().size());
        Assertions.assertTrue(companiesRelatedNames.get(16).possibleNames().contains("Baunti Agro Firma"));
    }

    @Test
    void parseCsvToCompaniesInfos_throwException_whenInvalidCsvStructureInARow() {
        //given
        String filePath = "testData/invalid1_company_list.csv";
        sut = new CompanyListParser(filePath);

        Map<Integer, CompanyCsv> companiesGeneralInfo = new HashMap<>();
        Map<Integer, Company > companiesRelatedNames = new HashMap<>();

        //when
        CsvException thrown = Assertions.assertThrows(CsvException.class, () -> {
            sut.parseCsvToCompaniesInfos(companiesGeneralInfo, companiesRelatedNames);
        });

        //then
        Assertions.assertEquals("Invalid CSV data: 3;Vola-vola Co; The", thrown.getMessage());
    }

    @Test
    void parseCsvToCompaniesInfos_throwException_whenEmptyRows() {
        //given
        String filePath = "testData/invalid2_company_list.csv";
        sut = new CompanyListParser(filePath);

        Map<Integer, CompanyCsv> companiesGeneralInfo = new HashMap<>();
        Map<Integer, Company > companiesRelatedNames = new HashMap<>();

        //when
        CsvException thrown = Assertions.assertThrows(CsvException.class, () -> {
            sut.parseCsvToCompaniesInfos(companiesGeneralInfo, companiesRelatedNames);
        });

        //then
        Assertions.assertEquals("Invalid CSV data: ", thrown.getMessage());
    }

}

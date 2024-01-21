package com.example.companyFame.service;

import com.example.companyFame.model.Company;
import com.example.companyFame.model.CompanyInfoCache;
import com.example.companyFame.model.NewspaperArticlesCache;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MatchersCountService implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchersCountService.class);
    private static final int CHECK_DELAY_MILLIS = 5_000;
    private static final int AGGREGATION_CONCURRENCY = 8;
    private static final int MILLIS_IN_SECOND = 1_000;

    private final CompanyInfoCache companyInfoCache;
    private final NewspaperArticlesCache newspaperArticlesCache;

    private final ExecutorService warmTassExecutorService;
    private final List<Future<?>> warmerWorkerFutures = new ArrayList<>();
    private final AtomicLong remainingCompaniesToCover = new AtomicLong(0);

    public MatchersCountService(CompanyInfoCache companyInfoCache, NewspaperArticlesCache newspaperArticlesCache) {
        this.companyInfoCache = companyInfoCache;
        this.newspaperArticlesCache = newspaperArticlesCache;

        this.warmTassExecutorService = Executors.newFixedThreadPool(AGGREGATION_CONCURRENCY);
    }

    /**
     * run the main task to calculate how many times companies are mentioned in articles
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<Integer, Company> companiesRelatedNames = companyInfoCache.getCompaniesRelatedNames();
        List<String> newspaperArticles = newspaperArticlesCache.getNewspaperArticles();

        long aggregationStartTime = System.currentTimeMillis();
        calculateNumberOfAllCompaniesMentions(companiesRelatedNames, newspaperArticles);
        long aggregationEndTime = System.currentTimeMillis();
        LOGGER.info("Aggregation time is " + (aggregationEndTime - aggregationStartTime) / MILLIS_IN_SECOND + " sec.");

        System.exit(0);
    }

    /**
     * create for each company a task to calculate mentions of companies and run in parallel threads
     * wait completion of all tasks
     */
    private void calculateNumberOfAllCompaniesMentions(Map<Integer, Company> companiesRelatedNames, List<String> newspaperArticles) {
        companiesRelatedNames.entrySet()
                .parallelStream()
                .forEach(entry -> {
                    Runnable warmingTask = new ArticleCheckTask(entry.getKey(), entry.getValue().possibleNames(), newspaperArticles);
                    Future<?> workerFuture = warmTassExecutorService.submit(warmingTask);
                    warmerWorkerFutures.add(workerFuture);
                    remainingCompaniesToCover.incrementAndGet();
                });
        waitForCurrentTasksCompletion();
    }

    @RequiredArgsConstructor
    private class ArticleCheckTask implements Runnable {
        private final int companyId;
        private final List<String> possibleCompanyNames;
        private final List<String> newspaperArticles;

        @Override
        public void run() {
            calculateNumberOfCompanyNameMentionsInAllArticles(companyId, possibleCompanyNames, newspaperArticles);
        }
    }

    /**
     * calculate company possible names mentions in all articles
     */
    public int calculateNumberOfCompanyNameMentionsInAllArticles(int companyId, List<String> possibleCompanyNames, List<String> newspaperArticles) {
        int totalNumberOfCompanyMentionsInAllArticles = possibleCompanyNames.parallelStream()
                .mapToInt(companyName ->
                        newspaperArticles.parallelStream()
                                .mapToInt(article -> StringUtils.countMatches(article, companyName))
                                .sum())
                .sum();

        if (totalNumberOfCompanyMentionsInAllArticles != 0) {
            LOGGER.info("Company \"" + companyInfoCache.getCompanyById(companyId).name() + "\""
                    + " is mentioned " + totalNumberOfCompanyMentionsInAllArticles + " times");
        }

        return totalNumberOfCompanyMentionsInAllArticles;
    }

    private void waitForCurrentTasksCompletion() {
        long companiesToCover = remainingCompaniesToCover.get();
        long lastTimeMills = System.currentTimeMillis();
        for (Future<?> workerFuture: warmerWorkerFutures) {
            try {
                do {
                    try {
                        workerFuture.get(CHECK_DELAY_MILLIS, TimeUnit.MILLISECONDS);
                    } catch (TimeoutException e) {
                        //NOP
                    }
                    /*
                    if (System.currentTimeMillis() - lastTimeMills > CHECK_DELAY_MILLIS) {
                        long loaded = companiesToCover - remainingCompaniesToCover.longValue();
                        LOGGER.info("Article parsing progress: " + String.format("%.3f%%", loaded * 100f / companiesToCover)
                                + ". Processed " + loaded + " of " + companiesToCover + " companies.");
                    }
                     */
                } while (!workerFuture.isDone());
            } catch (Exception ex) {
                LOGGER.error("Error during waiting for task completion", ex);
            }
        }
        warmerWorkerFutures.clear();
        remainingCompaniesToCover.set(0);
    }

}

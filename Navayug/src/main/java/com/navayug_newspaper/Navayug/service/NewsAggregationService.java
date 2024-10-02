package com.navayug_newspaper.Navayug.service;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.navayug_newspaper.Navayug.dto.BaseParams;
import com.navayug_newspaper.Navayug.dto.SearchArticleParams;
import com.navayug_newspaper.Navayug.model.MetaData;
import com.navayug_newspaper.Navayug.model.NewsSummaryData;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NewsAggregationService {

  @Autowired private GuardianNewsServiceImpl guardianNewsService;

  @Autowired private NewYorkTimesNewsServiceImpl newYorkTimesNewsService;

  @Cacheable(value = "newsCache", key = "#searchArticleParams.toString()")
  public NewsSummaryData getAggregatedNewsData(SearchArticleParams searchArticleParams) {

    TotalNewsCount totalResults = findTotal(searchArticleParams);
    Integer resultsOffset = searchArticleParams.getPageSize() * (searchArticleParams.getPageNumber() - 1);

    if (searchArticleParams.getPageNumber() > 1 && resultsOffset > totalResults.totalCount()) {
      log.error("Invalid PageNumber = {} & PageSize = {}", searchArticleParams.getPageNumber(),
          searchArticleParams.getPageSize());
      throw new InvalidParameterException(" invalid pageNumber ");
    }

    Boolean isGuardianSufficient = totalResults.getGuardianTotal() > resultsOffset &&
        totalResults.getGuardianTotal() >= resultsOffset + searchArticleParams.getPageSize();

    Boolean isBothAPIsNeeded = !isGuardianSufficient && totalResults.getGuardianTotal() > resultsOffset;

    if (isGuardianSufficient) {
      log.info(" only guardian api data is sufficient ");
      NewsSummaryData newsSummaryData = guardianNewsService.getNews((BaseParams)searchArticleParams,
          searchArticleParams.getSearchTerm());
      newsSummaryData.setMetaData(new MetaData(totalResults.totalCount()));
      return newsSummaryData;
    } else if (isBothAPIsNeeded) {

      NewsSummaryData newsSummaryDataGuardian = guardianNewsService.getNews((BaseParams)searchArticleParams,
          searchArticleParams.getSearchTerm());

      Integer remainingCount = resultsOffset + searchArticleParams.getPageSize() - totalResults.getGuardianTotal();
      NewsSummaryData newsSummaryDataNyTimes = new NewsSummaryData();
      newsSummaryDataNyTimes.setArticleDataList(new LinkedList<>());

      Integer startPageNumber = 0;
      getTheDataFromNewYorkTimes(searchArticleParams, remainingCount, newsSummaryDataNyTimes, startPageNumber, 0);

      newsSummaryDataGuardian.getArticleDataList().addAll(newsSummaryDataNyTimes.getArticleDataList());
      newsSummaryDataGuardian.setMetaData(new MetaData(totalResults.totalCount()));
      return newsSummaryDataGuardian;
    } else {
      log.info(" only new york times api data is sufficient ");
      NewsSummaryData newsSummaryDataNyTimes = new NewsSummaryData();
      newsSummaryDataNyTimes.setArticleDataList(new LinkedList<>());

      Integer startPageNumber = (resultsOffset - totalResults.getGuardianTotal()) / 10;
      Integer removeDuplicates = resultsOffset - totalResults.getGuardianTotal();

      Integer remainingCount = (resultsOffset + searchArticleParams.getPageSize() > totalResults.totalCount()) ?
          totalResults.totalCount() - resultsOffset :
          searchArticleParams.getPageSize();

      getTheDataFromNewYorkTimes(searchArticleParams, remainingCount, newsSummaryDataNyTimes, startPageNumber,
          removeDuplicates);
      newsSummaryDataNyTimes.setMetaData(new MetaData(totalResults.totalCount()));
      return newsSummaryDataNyTimes;
    }

  }

  private void getTheDataFromNewYorkTimes(
      SearchArticleParams baseParams,
      Integer remainingCount,
      NewsSummaryData newsSummaryDataNyTimes,
      Integer startPageNumber,
      Integer removeDuplicates) {
    while (remainingCount > 0) {
      log.info("running new york times api for pageNumber = {}", startPageNumber);
      baseParams.setPageNumber(startPageNumber);
      NewsSummaryData data = newYorkTimesNewsService.getNews((BaseParams)baseParams,
          baseParams.getSearchTerm());
      if (removeDuplicates > 0) {
        data.setArticleDataList(
            data.getArticleDataList().stream().skip(removeDuplicates).collect(Collectors.toList()));
      }
      if (remainingCount > 10) {
        newsSummaryDataNyTimes.getArticleDataList().addAll(data.getArticleDataList());
      } else {
        newsSummaryDataNyTimes.getArticleDataList()
            .addAll(data.getArticleDataList().stream().limit(remainingCount).collect(Collectors.toList()));
      }
      startPageNumber++;
      if (removeDuplicates > 0 && removeDuplicates > 10) {
        removeDuplicates = removeDuplicates - 10;
      } else if (removeDuplicates > 0) {
        remainingCount = remainingCount - 10 + removeDuplicates;
        removeDuplicates = 0;
      } else {
        remainingCount = remainingCount - 10;
      }
    }
  }

  private TotalNewsCount findTotal(SearchArticleParams baseParams) {
    Integer currentPageNumber = baseParams.getPageNumber();
    // guardian api page starts from 1
    baseParams.setPageNumber(1);
    NewsSummaryData newsSummaryData = null;
    TotalNewsCount totalResults = new TotalNewsCount();
    try {
      newsSummaryData = guardianNewsService.getNews((BaseParams)baseParams, baseParams.getSearchTerm());
      totalResults.setGuardianTotal(extractedTotal(newsSummaryData));
    } catch (HttpClientErrorException e) {
      totalResults.setGuardianTotal(0);
    }

    // nytimes api page starts from 0
    baseParams.setPageNumber(0);
    newsSummaryData = newYorkTimesNewsService.getNews((BaseParams)baseParams, baseParams.getSearchTerm());
    totalResults.setNyTimesTotal(extractedTotal(newsSummaryData));
    baseParams.setPageNumber(currentPageNumber);
    return totalResults;
  }

  @Data
  @NoArgsConstructor
  public class TotalNewsCount {
    private Integer guardianTotal;
    private Integer nyTimesTotal;

    public Integer totalCount() {
      return this.guardianTotal + this.nyTimesTotal;
    }
  }

  private Integer extractedTotal(NewsSummaryData newsSummaryData) {
    if (newsSummaryData != null && newsSummaryData.getMetaData() != null) {
      return newsSummaryData.getMetaData().getTotalCount();
    }
    return 0;
  }

  // Need to handle the total count correctly
  public NewsSummaryData getAggregatedNews(BaseParams baseParams) {
    try {
      NewsSummaryData guardianResponse = null;
      NewsSummaryData nytimesResponse = null;
      Boolean isToIncludeNyTimesResponse = false;
      NewsSummaryData newsSummaryData = new NewsSummaryData();
      Integer total = 0;
      Integer currentPageNumber = baseParams.getPageNumber();
      try {
        guardianResponse = guardianNewsService.getcurrentNews(baseParams);
        if (guardianResponse != null && guardianResponse.getArticleDataList() != null &&
            guardianResponse.getArticleDataList().size() > 0) {
          // for now we are hardcoding the pagesize to be 10 so eliminating the last page to simplify the logic
          total += (guardianResponse.getMetaData().getTotalCount() -
              (guardianResponse.getMetaData().getTotalCount() % 10));
        }
      } catch (HttpClientErrorException e) {
        // Occurs when the page size exceeds the results from guardian news paper
        isToIncludeNyTimesResponse = true;

      }

      if (isToIncludeNyTimesResponse ||
          guardianResponse.getArticleDataList().size() < baseParams.getPageSize()) {
        baseParams.setPageNumber(1);
        Integer maxPages = 1;
        try {
          guardianResponse = guardianNewsService.getcurrentNews(baseParams);
          if (guardianResponse != null && guardianResponse.getArticleDataList() != null &&
              guardianResponse.getArticleDataList().size() > 0) {
            // for now we are hardcoding the pagesize to be 10 so eliminating the last page to simplify the logic
            total += (guardianResponse.getMetaData().getTotalCount() -
                (guardianResponse.getMetaData().getTotalCount() % 10));
          }
          maxPages = guardianResponse.getMetaData().getTotalCount() / baseParams.getPageSize();
          // setting correct page number to call ny times
          baseParams.setPageNumber(currentPageNumber - maxPages - 1);
        } catch (HttpClientErrorException ex) {

        }
      }

      nytimesResponse = newYorkTimesNewsService.getcurrentNews(baseParams);
      if (nytimesResponse != null && nytimesResponse.getArticleDataList() != null &&
          nytimesResponse.getArticleDataList().size() > 0) {
        total += nytimesResponse.getMetaData().getTotalCount();
      }

      // check if the guardian news data is not enough for pagesize
      if (isToIncludeNyTimesResponse ||
          (guardianResponse != null && guardianResponse.getArticleDataList() != null &&
              !guardianResponse.getArticleDataList().isEmpty() &&
              guardianResponse.getArticleDataList().size() < baseParams.getPageSize())) {

        newsSummaryData.setArticleDataList(nytimesResponse.getArticleDataList());
      } else {
        newsSummaryData.setArticleDataList(guardianResponse.getArticleDataList());
      }

      newsSummaryData.setMetaData(new MetaData(total));
      return newsSummaryData;
    } catch (Exception e) {
      return null;
    }
  }

  public NewsSummaryData getAggregatedNewsBySearchTerm(SearchArticleParams searchArticleParams) {
    try {
      NewsSummaryData guardianResponse = null;
      NewsSummaryData nytimesResponse = null;
      Boolean isToIncludeNyTimesResponse = false;
      NewsSummaryData newsSummaryData = new NewsSummaryData();
      Integer total = 0;
      Integer currentPageNumber = searchArticleParams.getPageNumber();
      try {
        guardianResponse = guardianNewsService.getNewsBySearchTerm(searchArticleParams);
        if (guardianResponse != null && guardianResponse.getArticleDataList() != null &&
            guardianResponse.getArticleDataList().size() > 0) {
          // for now we are hardcoding the pagesize to be 10 so eliminating the last page to simplify the logic
          total += (guardianResponse.getMetaData().getTotalCount() -
              (guardianResponse.getMetaData().getTotalCount() % 10));
        }
      } catch (HttpClientErrorException e) {
        // Occurs when the page size exceeds the results from guardian news paper
        isToIncludeNyTimesResponse = true;

      }

      if (isToIncludeNyTimesResponse ||
          guardianResponse.getArticleDataList().size() < searchArticleParams.getPageSize()) {
        searchArticleParams.setPageNumber(1);
        Integer maxPages = 1;
        try {
          guardianResponse = guardianNewsService.getNewsBySearchTerm(searchArticleParams);
          if (guardianResponse != null && guardianResponse.getArticleDataList() != null &&
              guardianResponse.getArticleDataList().size() > 0) {
            // for now we are hardcoding the pagesize to be 10 so eliminating the last page to simplify the logic
            total += (guardianResponse.getMetaData().getTotalCount() -
                (guardianResponse.getMetaData().getTotalCount() % 10));
          }
          maxPages = guardianResponse.getMetaData().getTotalCount() / searchArticleParams.getPageSize();
          // setting correct page number to call ny times
          searchArticleParams.setPageNumber(currentPageNumber - maxPages - 1);
        } catch (HttpClientErrorException ex) {

        }
      }
      nytimesResponse = newYorkTimesNewsService.getNewsBySearchTerm(searchArticleParams);
      if (nytimesResponse != null && nytimesResponse.getArticleDataList() != null &&
          nytimesResponse.getArticleDataList().size() > 0) {
        total += nytimesResponse.getMetaData().getTotalCount();
      }

      // check if the guardian news data is not enough for pagesize
      if (isToIncludeNyTimesResponse ||
          (guardianResponse != null && guardianResponse.getArticleDataList() != null &&
              !guardianResponse.getArticleDataList().isEmpty() &&
              guardianResponse.getArticleDataList().size() < searchArticleParams.getPageSize())) {

        newsSummaryData.setArticleDataList(nytimesResponse.getArticleDataList());
      } else {
        newsSummaryData.setArticleDataList(guardianResponse.getArticleDataList());
      }

      newsSummaryData.setMetaData(new MetaData(total));
      return newsSummaryData;
    } catch (Exception e) {
      return null;
    }
  }
}

package com.navayug_newspaper.Navayug.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.navayug_newspaper.Navayug.dto.BaseParams;
import com.navayug_newspaper.Navayug.dto.ResponseGuardianDTO;
import com.navayug_newspaper.Navayug.dto.SearchArticleParams;
import com.navayug_newspaper.Navayug.exception.InvalidAPIKeyException;
import com.navayug_newspaper.Navayug.model.NewsSummaryData;
import com.navayug_newspaper.Navayug.util.HashUtil;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GuardianNewsServiceImpl implements NewsService {
  private static final String API_URL = "https://content.guardianapis.com/search";
  private static final String SERVICE_NAME = "guardian";

  @Value("${guardian.hashedApiKey}") private String hashedApiKey;

  @Override
  public NewsSummaryData getcurrentNews(BaseParams baseParams) {
    return getNews(baseParams, null);
  }

  @Override
  public NewsSummaryData getNewsBySearchTerm(SearchArticleParams searchArticleParams) {
    return getNews((BaseParams)searchArticleParams, searchArticleParams.getSearchTerm());
  }

  @Override
  @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "fallbackMethod")
  public NewsSummaryData getNews(BaseParams baseParams, String searchTerm) {
    if (!HashUtil.hashApiKey(baseParams.getGuardianAPIKey()).equals(hashedApiKey)) {
      throw new InvalidAPIKeyException("Error the api key given for guardian is Invalid");
    }

    String url = searchTerm != null ?
        String.format("%s?q=%s&page=%d&page-size=%d&from-date=%s&to-date=%s&api-key=%s", API_URL,
            searchTerm, baseParams.getPageNumber(), baseParams.getPageSize(), baseParams.getFromDate(),
            baseParams.getToDate(), baseParams.getGuardianAPIKey()) :
        String.format("%s?page=%d&page-size=%d&from-date=%s&to-date=%s&api-key=%s", API_URL,
            baseParams.getPageNumber(), baseParams.getPageSize(), baseParams.getFromDate(),
            baseParams.getToDate(), baseParams.getGuardianAPIKey());

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<ResponseGuardianDTO> response = restTemplate.getForEntity(url, ResponseGuardianDTO.class);
    return response.getBody().convertToNewsSummaryData();
  }

  private NewsSummaryData fallbackMethod(Exception e) {
    log.info("Got exception in the service: {} & the error msg is: {}", SERVICE_NAME, e.getMessage());
    return new NewsSummaryData();
  }
}

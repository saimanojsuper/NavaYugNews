package com.navayug_newspaper.Navayug.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.navayug_newspaper.Navayug.dto.BaseParams;
import com.navayug_newspaper.Navayug.dto.ResponseNewYorkTimesDTO;
import com.navayug_newspaper.Navayug.dto.SearchArticleParams;
import com.navayug_newspaper.Navayug.exception.InvalidAPIKeyException;
import com.navayug_newspaper.Navayug.model.NewsSummaryData;
import com.navayug_newspaper.Navayug.util.HashUtil;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NewYorkTimesNewsServiceImpl implements NewsService {

  private static final String API_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
  private static final String Dummy_URL = "https://api.nytimes.com/svc/searchs/v2/articlesearch.json";

  @Value("${nytimes.hashedApiKey}") private String hashedApiKey;
  private static final String SERVICE_NAME = "new-york-times";

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
    if (!HashUtil.hashApiKey(baseParams.getNyTimesAPIKey()).equals(hashedApiKey)) {
      throw new InvalidAPIKeyException("Error the api key given for new your times is Invalid");
    }

    String url = searchTerm != null ?
        String.format("%s?q=%s&page=%d&begin_date=%s&end_date=%s&sort=newest&api-key=%s", API_URL, searchTerm,
            baseParams.getPageNumber(), baseParams.getFromDate(), baseParams.getToDate(),
            baseParams.getNyTimesAPIKey()) :
        String.format("%s?page=%d&begin_date=%s&end_date=%s&sort=newest&api-key=%s", API_URL,
            baseParams.getPageNumber(), baseParams.getFromDate(), baseParams.getToDate(),
            baseParams.getNyTimesAPIKey());
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<ResponseNewYorkTimesDTO> response = restTemplate.getForEntity(url,
        ResponseNewYorkTimesDTO.class);
    return response.getBody().convertToNewsSummaryData();
  }

  private NewsSummaryData fallbackMethod(Exception e) {
    log.info("Got exception in the service: {} & the error msg is: {}", SERVICE_NAME, e.getMessage());
    return new NewsSummaryData();
  }
}

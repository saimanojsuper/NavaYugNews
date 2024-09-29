package com.navayug_newspaper.Navayug.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.navayug_newspaper.Navayug.dto.BaseParams;
import com.navayug_newspaper.Navayug.dto.ResponseNewYorkTimesDTO;
import com.navayug_newspaper.Navayug.dto.SearchArticleParams;
import com.navayug_newspaper.Navayug.model.NewsSummaryData;

@Service
public class NewYorkTimesNewsServiceImpl implements NewsService {

  private static final String API_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

  @Value("${nytimes.apiKey}")
  private String apiKey;
  @Override
  public NewsSummaryData getcurrentNews(BaseParams baseParams) {
    String url = String.format("%s?page=%d&begin_date=%s&end_date=%s&sort=newest&api-key=%s", API_URL,
        baseParams.getPageNumber(), baseParams.getFromDate(), baseParams.getToDate(), apiKey);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<ResponseNewYorkTimesDTO> response= restTemplate.getForEntity(url, ResponseNewYorkTimesDTO.class);
    return response.getBody().convertToNewsSummaryData();
  }

  @Override
  public NewsSummaryData getNewsBySearchTerm(SearchArticleParams searchArticleParams) {
    return null;
  }
}

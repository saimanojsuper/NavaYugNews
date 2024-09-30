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

@Service
public class GuardianNewsServiceImpl implements NewsService {
  private static final String API_URL = "https://content.guardianapis.com/search";

  @Value("${guardian.hashedApiKey}") private String hashedApiKey;

  @Override
  public NewsSummaryData getcurrentNews(BaseParams baseParams) {

    if (!HashUtil.hashApiKey(baseParams.getGuardianAPIKey()).equals(hashedApiKey)) {
      throw new InvalidAPIKeyException("Error the api key given for guardian is Invalid");
    }

    String url = String.format("%s?page=%d&page-number-%d&from-date=%s&to-date=%s&api-key=%s", API_URL,
        baseParams.getPageNumber(), baseParams.getPageSize(), baseParams.getFromDate(),
        baseParams.getToDate(), baseParams.getGuardianAPIKey());
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<ResponseGuardianDTO> response = restTemplate.getForEntity(url, ResponseGuardianDTO.class);
    return response.getBody().convertToNewsSummaryData();
  }

  @Override
  public NewsSummaryData getNewsBySearchTerm(SearchArticleParams searchArticleParams) {
    //    String url = String.format("%s?q=%s&page=%d&api-key=%s", API_URL, query, page, apiKey);
    return null;
  }
}

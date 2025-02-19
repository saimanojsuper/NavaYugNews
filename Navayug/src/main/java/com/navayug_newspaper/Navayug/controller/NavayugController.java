package com.navayug_newspaper.Navayug.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.navayug_newspaper.Navayug.dto.BaseParams;
import com.navayug_newspaper.Navayug.dto.SearchArticleParams;
import com.navayug_newspaper.Navayug.model.NewsSummaryData;
import com.navayug_newspaper.Navayug.service.NewsAggregationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Validated
@RequestMapping("/api/")
@Slf4j
public class NavayugController {

  @Autowired NewsAggregationService newsAggregationService;

  @PostMapping(value = "currentNews", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public NewsSummaryData getCurrentNews(@RequestBody BaseParams filterParams) {
    return newsAggregationService.getAggregatedNews(filterParams);
  }

  @PostMapping(value = "currentNewsBySearch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public NewsSummaryData getCurrentNewsBySearch(@RequestBody SearchArticleParams searchArticleParams) {
    log.info("came inside the currentNewsBySearch Controller check hashcode {}", searchArticleParams.hashCode());
    return newsAggregationService.getAggregatedNewsData(searchArticleParams);
  }

}

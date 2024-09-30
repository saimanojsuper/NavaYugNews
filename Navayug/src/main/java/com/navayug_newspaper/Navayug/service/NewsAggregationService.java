package com.navayug_newspaper.Navayug.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.navayug_newspaper.Navayug.dto.BaseParams;
import com.navayug_newspaper.Navayug.model.ArticleData;
import com.navayug_newspaper.Navayug.model.MetaData;
import com.navayug_newspaper.Navayug.model.NewsSummaryData;

@Service
public class NewsAggregationService {

  @Autowired private GuardianNewsServiceImpl guardianNewsServiceImpl;

  @Autowired private NewYorkTimesNewsServiceImpl newYorkTimesNewsService;

  // Need to handle the total count correctly
  public NewsSummaryData getAggregatedNews(BaseParams baseParams) {
    try {
      NewsSummaryData guardianResponse = null;
      Boolean isToIncludeNyTimesResponse = false;
      try {
        guardianResponse = guardianNewsServiceImpl.getcurrentNews(baseParams);
      } catch (Exception e) {
        isToIncludeNyTimesResponse = true;
      }

      NewsSummaryData nytimesResponse = newYorkTimesNewsService.getcurrentNews(baseParams);

      NewsSummaryData newsSummaryData = new NewsSummaryData();
      Integer total = 0;
      List<ArticleData> combineData = new ArrayList<>();
      if (guardianResponse != null && guardianResponse.getArticleDataList() != null &&
          guardianResponse.getArticleDataList().size() > 0) {
        total += guardianResponse.getMetaData().getTotalCount();
        combineData.addAll(guardianResponse.getArticleDataList());
      }
      if (nytimesResponse != null && nytimesResponse.getArticleDataList() != null &&
          nytimesResponse.getArticleDataList().size() > 0) {
        total += nytimesResponse.getMetaData().getTotalCount();
        combineData.addAll(nytimesResponse.getArticleDataList());
      }

      newsSummaryData.setMetaData(new MetaData(total));
      newsSummaryData.setArticleDataList(combineData);

      return newsSummaryData;
    } catch (Exception e) {
      return null;
    }
  }
}

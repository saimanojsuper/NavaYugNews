package com.navayug_newspaper.Navayug.service;

import com.navayug_newspaper.Navayug.dto.BaseParams;
import com.navayug_newspaper.Navayug.dto.SearchArticleParams;
import com.navayug_newspaper.Navayug.model.NewsSummaryData;

public interface NewsService {
  NewsSummaryData getcurrentNews(BaseParams baseParams);
  NewsSummaryData getNewsBySearchTerm(SearchArticleParams searchArticleParams);
  NewsSummaryData getNews(BaseParams searchArticleParams, String searchTerm);

}

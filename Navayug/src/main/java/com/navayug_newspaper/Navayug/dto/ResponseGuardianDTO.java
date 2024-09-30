package com.navayug_newspaper.Navayug.dto;

import java.util.List;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;

import com.navayug_newspaper.Navayug.model.ArticleData;
import com.navayug_newspaper.Navayug.model.MetaData;
import com.navayug_newspaper.Navayug.model.NewsSummaryData;

import lombok.Data;

@Data
public class ResponseGuardianDTO {
  private ResponseGuardian response;

  @Data
  public static class ResponseGuardian {
    private int total;
    private List<ResultDTO> results;
  }

  @Data
  public static class ResultDTO {
    private String id;
    private String webTitle;
    private String webUrl;
    private String pillarName;
    private String sectionName;
    private String webPublicationDate;

  }

  public NewsSummaryData convertToNewsSummaryData() {
    NewsSummaryData newsSummaryData = new NewsSummaryData();
    MetaData metaData = new MetaData();
    metaData.setTotalCount(response.total);
    newsSummaryData.setMetaData(metaData);
    List<ArticleData> articleDataList =
        response.results != null ? response.results.stream().map(resultDTO -> {
          ArticleData articleData = new ArticleData();
          articleData.setId(resultDTO.getId());
          articleData.setHeadline(resultDTO.getWebTitle());
          articleData.setArticleUrl(resultDTO.getWebUrl());
          articleData.setGenre(resultDTO.getPillarName());
          articleData.setPublishedTime(ZonedDateTime.parse(resultDTO.getWebPublicationDate()).toLocalDateTime());
          articleData.setSectionName(resultDTO.getSectionName());
          return articleData;
        }).collect(Collectors.toList()) : null;
    newsSummaryData.setArticleDataList(articleDataList);
    return newsSummaryData;
  }
}

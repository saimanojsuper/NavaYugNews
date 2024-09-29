package com.navayug_newspaper.Navayug.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.navayug_newspaper.Navayug.model.ArticleData;
import com.navayug_newspaper.Navayug.model.MetaData;
import com.navayug_newspaper.Navayug.model.NewsSummaryData;

import lombok.Data;

@Data
public class ResponseNewYorkTimesDTO {
  private ResponseNewYorkTimes response;
  private String status;
  private String copyright;

  @Data
  public static class ResponseNewYorkTimes {
    private Meta meta;
    private List<Docs> docs;
  }

  @Data
  public static class Meta {
    private Integer hits;
  }

  @Data
  public static class Docs {

    private String snippet;
    private String web_url;

    private String pub_date;
    private String section_name;

    private String type_of_material;

    private HeadLine headline;

    private List<ImageUrl> multimedia;

  }

  @Data
  public static class HeadLine {
    private String print_headline;
  }

  @Data
  public static class ImageUrl {
    private String url;
    private String height;
    private String width;

  }

  public NewsSummaryData convertToNewsSummaryData() {
    NewsSummaryData newsSummaryData = new NewsSummaryData();
    MetaData metaData = new MetaData();
    metaData.setTotalCount(response.getMeta().getHits());
    newsSummaryData.setMetaData(metaData);
    List<ArticleData> articleDataList =
        response.getDocs() != null ? response.getDocs().stream().map(docsDto -> {
          ArticleData articleData = new ArticleData();
          articleData.setHeadline(docsDto.getHeadline().getPrint_headline());
          articleData.setArticleUrl(docsDto.getWeb_url());
          articleData.setGenre(docsDto.getType_of_material());

          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

          // Parse the string into an OffsetDateTime object
          OffsetDateTime offsetDateTime = OffsetDateTime.parse(docsDto.getPub_date(), formatter);

          // Convert it to LocalDate
          LocalDateTime localDate = offsetDateTime.toLocalDateTime();

          articleData.setPublishedTime(localDate);
          articleData.setSectionName(docsDto.getSection_name());
          articleData.setDescription(docsDto.getSnippet());
          return articleData;
        }).collect(Collectors.toList()) : null;
    newsSummaryData.setArticleDataList(articleDataList);
    return newsSummaryData;
  }
}

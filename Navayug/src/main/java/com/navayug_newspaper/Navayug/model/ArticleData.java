package com.navayug_newspaper.Navayug.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleData {
  private String id;
  private String Headline;
  //Not present for guardian directly but we can figureout using the articleUrl in
  private String description;
  private String sectionName;
  private String genre;
  private String articleAuthor;
  private String articleUrl; //This can be referred from the hateos
  private LocalDateTime publishedTime;
  private String imageUrl;

}

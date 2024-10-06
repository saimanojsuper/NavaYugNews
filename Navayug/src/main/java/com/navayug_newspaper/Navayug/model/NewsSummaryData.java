package com.navayug_newspaper.Navayug.model;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class NewsSummaryData {
  private MetaData metaData;
  private List<ArticleData> articleDataList;

  public NewsSummaryData(){
    this.metaData = new MetaData(0);
    this.articleDataList = new LinkedList<>();
  }
}

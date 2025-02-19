package com.navayug_newspaper.Navayug.dto;

import lombok.Data;

@Data
public class SearchArticleParams extends BaseParams {
  private String searchTerm;

  @Override
  public String toString() {
    return "SearchArticleParams{" + "searchTerm='" + searchTerm + '\'' + "} " + super.toString();
  }
}

package com.navayug_newspaper.Navayug.dto;

import lombok.Data;

@Data
public class BaseParams {
  //Validate and assign the default values
  private Integer pageNumber;
  private Integer pageSize;
  private String fromDate;
  private String toDate;
  private String guardianAPIKey;
  private String nyTimesAPIKey;

}

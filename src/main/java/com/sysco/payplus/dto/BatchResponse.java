package com.sysco.payplus.dto;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 4/1/20 Time: 12:54 PM
 */

public class BatchResponse {
  private final List<Object> successfulItems;
  private final List<ErrorDTO> failedItems;

  public BatchResponse() {
    this.successfulItems = new LinkedList<>();
    this.failedItems = new LinkedList<>();
  }

  public BatchResponse(List<Object> successfulItems, List<ErrorDTO> failedItems) {
    this.successfulItems = successfulItems;
    this.failedItems = failedItems;
  }

  public List<Object> getSuccessfulItems() {
    return successfulItems;
  }

  public List<ErrorDTO> getFailedItems() {
    return failedItems;
  }

  public void addSuccessfulItem(Object item) {
    this.successfulItems.add(item);
  }

  public void addFailedItem(ErrorDTO errorDTO) {
    this.failedItems.add(errorDTO);
  }
}

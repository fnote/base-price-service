package com.sysco.rps.dto;

import com.sysco.rps.dto.masterdata.BaseGlobalDTO;
import java.util.List;

public class ListResponse<T extends BaseGlobalDTO> {
  private long totalItems;
  private int totalPages;
  private List<T> items;

  public ListResponse() {

  }

  public ListResponse(long totalItems, int totalPages, List<T> items) {
    this.totalItems = totalItems;
    this.totalPages = totalPages;
    this.items = items;
  }

  public long getTotalItems() {
    return totalItems;
  }

  public void setTotalItems(long totalItems) {
    this.totalItems = totalItems;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public List<T> getItems() {
    return items;
  }

  public void setItems(List<T> items) {
    this.items = items;
  }
}

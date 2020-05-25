package com.sysco.rps.service.exception;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/24/20 Time: 12:54 PM
 */

public class RecordLockedException extends Exception {

  public RecordLockedException(String message) {
    super(message);
  }
}

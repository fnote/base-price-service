package com.sysco.payplus.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableAutoConfiguration
public class HikariIntegrationTest {

  @Autowired
  private DataSource dataSource;

  @Test
  public void test() {
    assertEquals("com.zaxxer.hikari.HikariDataSource", dataSource.getClass().getName());
  }
}
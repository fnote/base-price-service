package com.sysco.payplus.service.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sysco.payplus.dto.security.IntrospectionResponse;
import com.sysco.payplus.dto.security.Role;
import com.sysco.payplus.dto.security.UserDetails;
import com.sysco.payplus.entity.security.Authority;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@EnableAutoConfiguration
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class IntrospectRestClientServiceTest {

  private static String token = "qwertyuiop";
  @Value("${introspect.url}")
  public String introspectUrl;
  IntrospectRestClientService introspectRestClientService;
  @Mock
  private RestTemplate restTemplate;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    introspectRestClientService = new IntrospectRestClientServiceImpl(restTemplate);
  }

  @Test
  public void withProperToken() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("token", token);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<MultiValueMap<String, String>> dataMap =
        new HttpEntity<>(map, headers);

    IntrospectionResponse introspectionResponse = new IntrospectionResponse();
    introspectionResponse.setActive(true);
    UserDetails userDetails = new UserDetails();
    userDetails.setUserName("test_user");
    Set<String> scopes = new HashSet<>();
    scopes.add("123456");
    userDetails.setScopes(scopes);
    Role role = new Role();
    role.setCode("1");
    role.setName(Authority.USER.toString());
    List<Role> roles = new ArrayList<>();
    roles.add(role);
    userDetails.setRoles(roles);
    introspectionResponse.setUserDetails(userDetails);

    Mockito.when(restTemplate.postForObject(introspectUrl, dataMap, IntrospectionResponse.class)).thenReturn(introspectionResponse);
    IntrospectionResponse response = introspectRestClientService.getUserDataFromIntrospectService(token);
    assertTrue(response.isActive());
    assertEquals(introspectionResponse.getUserDetails().getUserName(), response.getUserDetails().getUserName());
    assertTrue(response.getUserDetails().getScopes().contains("123456"));
    assertEquals(1, response.getUserDetails().getRoles().size());
    assertEquals(introspectionResponse.getUserDetails().getRoles().get(0), response.getUserDetails().getRoles().get(0));
  }

  @Test
  public void withInvalidToken() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("token", token);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<MultiValueMap<String, String>> dataMap =
        new HttpEntity<>(map, headers);

    IntrospectionResponse introspectionResponse = new IntrospectionResponse();
    introspectionResponse.setActive(false);

    Mockito.when(restTemplate.postForObject(introspectUrl, dataMap, IntrospectionResponse.class)).thenReturn(introspectionResponse);
    IntrospectionResponse response = introspectRestClientService.getUserDataFromIntrospectService(token);
    assertFalse(response.isActive());
  }

  @Test
  public void withNullToken() {
    token = null;
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("token", token);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<MultiValueMap<String, String>> dataMap =
        new HttpEntity<>(map, headers);

    IntrospectionResponse introspectionResponse = new IntrospectionResponse();
    introspectionResponse.setActive(false);

    Mockito.when(restTemplate.postForObject(introspectUrl, dataMap, IntrospectionResponse.class)).thenReturn(introspectionResponse);
    IntrospectionResponse response = introspectRestClientService.getUserDataFromIntrospectService(token);
    assertFalse(response.isActive());
  }

}

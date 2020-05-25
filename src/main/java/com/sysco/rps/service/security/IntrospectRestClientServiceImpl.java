package com.sysco.rps.service.security;

import com.sysco.rps.dto.security.IntrospectionResponse;
import com.sysco.rps.filter.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class IntrospectRestClientServiceImpl implements IntrospectRestClientService {

  @Value("${resolved.introspect.url}")
  private String introspectUrl;

  private RestTemplate restTemplate;

  @Autowired
  IntrospectRestClientServiceImpl(RestTemplateBuilder restTemplateBuilder) {
    this(restTemplateBuilder
        .build());
  }

  IntrospectRestClientServiceImpl(RestTemplate template) {
    this.restTemplate = template;
  }

  public IntrospectionResponse getUserDataFromIntrospectService(String token) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add(SecurityConstants.INTROSPECT_TOKEN, token);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<MultiValueMap<String, String>> dataMap =
        new HttpEntity<>(map, headers);
    return restTemplate.postForObject(introspectUrl, dataMap, IntrospectionResponse.class);
  }
}

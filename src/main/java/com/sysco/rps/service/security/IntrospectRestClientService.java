package com.sysco.rps.service.security;

import com.sysco.rps.dto.security.IntrospectionResponse;

// TODO add a repository layer to make http requests
public interface IntrospectRestClientService {

    IntrospectionResponse getUserDataFromIntrospectService(String token);
}

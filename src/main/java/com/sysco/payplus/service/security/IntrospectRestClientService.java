package com.sysco.payplus.service.security;

import com.sysco.payplus.dto.security.IntrospectionResponse;

// TODO add a repository layer to make http requests
public interface IntrospectRestClientService {

    IntrospectionResponse getUserDataFromIntrospectService(String token);
}

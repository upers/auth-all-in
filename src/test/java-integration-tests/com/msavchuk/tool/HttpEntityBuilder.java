package com.msavchuk.tool;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HttpEntityBuilder {

    /**
     * Build empty entity with authorization header from parameter.
     * @param auth
     * @return
     */
    public static HttpEntity emptyAuthEntity(String auth) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, auth);

        return new HttpEntity(headers);
    }

    /**
     * Build entity with authorization header from auth parameter.
     * @param auth
     * @param body
     * @return
     */
    public static <T> HttpEntity<T> authEntity(String auth, T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, auth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity(body, headers);
    }
}

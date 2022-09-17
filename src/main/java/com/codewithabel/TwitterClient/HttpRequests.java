/*
 *
 * Code With Abel
 *
 * This code is free software; you can redistribute it and/or modify it
 *
 */
package com.codewithabel.TwitterClient;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

class HttpRequests {

    private final RestTemplate restTemplate;

    private HttpRequests(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Http POST Request
     *
     * @param token        User Token
     * @param url          URL to POST Request
     * @param data         Body of the request
     * @param responseType Type of Response
     * @param variable     Optional variables if there is a placeholder in the url
     * @return Response Entity
     */
    <T> ResponseEntity<T> postForEntity(
            String token,
            String url,
            String data,
            Class<T> responseType,
            Object... variable
    ) {
        return restTemplate.postForEntity(url,
                getHttpEntity(token, data),
                responseType,
                variable
        );

    }

    /**
     * Http POST Request
     *
     * @param token        User Token
     * @param url          URL to POST Request
     * @param data         Body of the request
     * @param responseType Type of Response
     * @return Response Entity
     */
    <T> ResponseEntity<T> postForEntity(
            String token,
            String url,
            String data,
            Class<T> responseType
    ) {
        return restTemplate.postForEntity(url,
                getHttpEntity(token, data),
                responseType
        );
    }

    /**
     * Http POST Request
     *
     * @param encodedClient   Base64 Encoded Client
     * @param url          URL to POST Request
     * @param data         Body of the request
     * @param responseType Type of Response
     * @return Response Entity
     */
    <T> ResponseEntity<T> postForEntity(
            String encodedClient,
            String url,
            MultiValueMap<String, String> data,
            Class<T> responseType
    ) {
        return restTemplate.postForEntity(url,
                getHttpEntity(encodedClient, data),
                responseType
        );
    }

    /**
     * Http POST Request
     *
     * @param token        User Token
     * @param url          URL to GET Request
     * @param responseType Type of Response
     * @return Response Entity
     */
    <T> ResponseEntity<T> getForEntity(
            String token,
            String url,
            Class<T> responseType
    ) {
        HttpEntity<String> httpEntity = new HttpEntity<>(getHeaders(token));
        return restTemplate.exchange(url,
                HttpMethod.GET,
                httpEntity,
                responseType
        );

    }

    private HttpEntity<String> getHttpEntity(String token, String requestJson) {
        return new HttpEntity<>(requestJson, getHeaders(token));
    }

    private HttpEntity<MultiValueMap<String, String>> getHttpEntity(String encodedClient,
                                                                    MultiValueMap<String, String> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(encodedClient);
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));

        return new HttpEntity<>(body, headers);
    }

    private HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));

        return headers;
    }
}

package com.azhar.urlshortener.service;

import com.azhar.urlshortener.dto.UrlRequest;
import com.azhar.urlshortener.dto.UrlResponse;

public interface UrlService {

    UrlResponse createShortUrl(UrlRequest urlRequest);

    UrlResponse getOriginalUrl(String shortCode);

    UrlResponse getUrlStats(String shortCode);

    void deleteExpiredUrls();
}

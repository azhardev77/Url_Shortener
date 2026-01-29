package com.azhar.urlshortener.service;

import com.azhar.urlshortener.dto.UrlRequest;
import com.azhar.urlshortener.dto.UrlResponse;
import com.azhar.urlshortener.entity.UrlEntity;
import com.azhar.urlshortener.repository.UrlRepository;
import com.azhar.urlshortener.util.HashGenerator;
import com.azhar.urlshortener.util.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class UrlServiceImpl implements UrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private UrlValidator urlValidator;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.url.expiry-days:30}")
    private int defaultExpiryDays;

    @Override
    @Transactional
    public UrlResponse createShortUrl(UrlRequest urlRequest) {
        // Validate URL
        if (!urlValidator.isValidUrl(urlRequest.getUrl())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL format");
        }

        String normalizedUrl = urlValidator.normalizeUrl(urlRequest.getUrl());

        // Check if URL already exists
        UrlEntity existingUrl = urlRepository.findByOriginalUrl(normalizedUrl).orElse(null);
        if (existingUrl != null) {
            return convertToResponse(existingUrl);
        }

        // Generate short code
        String shortCode;
        if (urlRequest.getCustomAlias() != null && !urlRequest.getCustomAlias().isEmpty()) {
            shortCode = hashGenerator.generateCustomShortCode(urlRequest.getCustomAlias());

            // Check if custom alias already exists
            if (urlRepository.existsByShortCode(shortCode)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Custom alias already exists. Please choose a different one.");
            }
        } else {
            // Generate unique short code
            shortCode = generateUniqueShortCode(normalizedUrl);
        }

        // Set expiry date
        LocalDateTime expiryDate = urlRequest.getExpiryDate();
        if (expiryDate == null) {
            expiryDate = LocalDateTime.now().plusDays(defaultExpiryDays);
        }

        // Create and save URL entity
        UrlEntity urlEntity = new UrlEntity(normalizedUrl, shortCode, expiryDate);
        urlRepository.save(urlEntity);

        return convertToResponse(urlEntity);
    }

    private String generateUniqueShortCode(String url) {
        String shortCode;
        int attempts = 0;
        final int MAX_ATTEMPTS = 10;

        do {
            if (attempts == 0) {
                shortCode = hashGenerator.generateShortCode(url + System.currentTimeMillis());
            } else {
                shortCode = hashGenerator.generateRandomString();
            }
            attempts++;
        } while (urlRepository.existsByShortCode(shortCode) && attempts < MAX_ATTEMPTS);

        if (attempts >= MAX_ATTEMPTS) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to generate unique short code");
        }

        return shortCode;
    }

    @Override
    @Transactional
    public UrlResponse getOriginalUrl(String shortCode) {
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Short URL not found"));

        // Check if URL has expired
        if (urlEntity.getExpiryDate() != null
                && urlEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "URL has expired");
        }

        // Increment click count
        urlEntity.incrementClickCount();
        urlRepository.save(urlEntity);

        return convertToResponse(urlEntity);
    }

    @Override
    public UrlResponse getUrlStats(String shortCode) {
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Short URL not found"));

        return convertToResponse(urlEntity);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    public void deleteExpiredUrls() {
        LocalDateTime now = LocalDateTime.now();
        long expiredCount = urlRepository.countExpiredUrls(now);

        if (expiredCount > 0) {
            urlRepository.deleteExpiredUrls(now);
        }
    }

    private UrlResponse convertToResponse(UrlEntity urlEntity) {
        String shortUrl = baseUrl + "/" + urlEntity.getShortCode();

        return new UrlResponse(
                urlEntity.getOriginalUrl(),
                shortUrl,
                urlEntity.getShortCode(),
                urlEntity.getCreatedAt(),
                urlEntity.getExpiryDate(),
                urlEntity.getClickCount()
        );
    }
}

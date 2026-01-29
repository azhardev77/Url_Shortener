package com.azhar.urlshortener.repository;

import com.azhar.urlshortener.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    
    Optional<UrlEntity> findByShortCode(String shortCode);
    
    Optional<UrlEntity> findByOriginalUrl(String originalUrl);
    
    boolean existsByShortCode(String shortCode);
    
    @Modifying
    @Query("DELETE FROM UrlEntity u WHERE u.expiryDate < :currentDate")
    void deleteExpiredUrls(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT COUNT(u) FROM UrlEntity u WHERE u.expiryDate < :currentDate")
    long countExpiredUrls(@Param("currentDate") LocalDateTime currentDate);
}
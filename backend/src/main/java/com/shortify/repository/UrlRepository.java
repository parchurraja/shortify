package com.shortify.repository;

import com.shortify.entity.Url;
import com.shortify.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    
    Optional<Url> findByShortCode(String shortCode);
    
    boolean existsByShortCode(String shortCode);
    
    Page<Url> findByUserAndDeletedAtIsNull(User user, Pageable pageable);
    
    long countByUserAndDeletedAtIsNull(User user);
    
    @Query("SELECT u FROM Url u WHERE u.user = :user AND u.deletedAt IS NULL AND " +
           "(LOWER(u.originalUrl) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.customAlias) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.shortCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Url> searchUrls(@Param("user") User user, @Param("search") String search, Pageable pageable);
}

package com.shortify.repository;

import com.shortify.entity.UrlClick;
import com.shortify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlClickRepository extends JpaRepository<UrlClick, Long> {

    // Projection interface for date-based click aggregation
    interface ClickDateCount {
        Object getDate();
        Long getClicks();
    }

    // Projection interface for generic group-by stats (Device, OS, Browser)
    interface GroupCount {
        String getName();
        Long getValue();
    }

    @Query("SELECT COUNT(c) FROM UrlClick c WHERE c.url.user = :user")
    long countAllClicksByUser(@Param("user") User user);

    @Query("SELECT COUNT(c) FROM UrlClick c WHERE c.url.user = :user AND c.clickedAt >= :since")
    long countClicksSinceByUser(@Param("user") User user, @Param("since") LocalDateTime since);

    @Query("SELECT FUNCTION('DATE', c.clickedAt) AS date, COUNT(c) AS clicks " +
           "FROM UrlClick c " +
           "WHERE c.url.user = :user AND c.clickedAt >= :since " +
           "GROUP BY FUNCTION('DATE', c.clickedAt) " +
           "ORDER BY FUNCTION('DATE', c.clickedAt) ASC")
    List<ClickDateCount> getClicksPastWeekByUser(@Param("user") User user, @Param("since") LocalDateTime since);


    @Query("SELECT c.device as name, COUNT(c) as value FROM UrlClick c WHERE c.url.user = :user GROUP BY c.device")
    List<GroupCount> getDeviceStatsByUser(@Param("user") User user);

    @Query("SELECT c.os as name, COUNT(c) as value FROM UrlClick c WHERE c.url.user = :user GROUP BY c.os")
    List<GroupCount> getOsStatsByUser(@Param("user") User user);

    @Query("SELECT c.browser as name, COUNT(c) as value FROM UrlClick c WHERE c.url.user = :user GROUP BY c.browser")
    List<GroupCount> getBrowserStatsByUser(@Param("user") User user);
}

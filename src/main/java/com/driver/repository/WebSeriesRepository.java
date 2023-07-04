package com.driver.repository;

import com.driver.model.WebSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WebSeriesRepository extends JpaRepository<WebSeries,Integer> {

    WebSeries findBySeriesName(String seriesName);

    @Query("SELECT AVG(w.rating) FROM WebSeries w")
    Double getAverageOfRating();
}

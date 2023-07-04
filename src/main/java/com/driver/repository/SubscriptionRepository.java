package com.driver.repository;

import com.driver.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubscriptionRepository extends JpaRepository<Subscription,Integer> {
    @Query("SELECT SUM(s.totalAmountPaid) FROM Subscription s")
    Integer getTotalRevenue();
}

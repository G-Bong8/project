package com.example.skip.repository;

import com.example.skip.entity.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("""
    SELECT SUM(p.totalPrice) 
    FROM Payment p 
    WHERE p.createdAt >= :start AND p.createdAt < :end AND p.status = 'PAID'
    """)
    Double getTotalPaidPriceBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
    SELECT COUNT(p) 
    FROM Payment p 
    WHERE p.createdAt >= :start 
    AND p.createdAt < :end AND p.status = 'PAID'
""")
    Long getPaidCountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
    SELECT COALESCE(SUM(p.totalPrice), 0)
    FROM Payment p
    WHERE p.status = 'PAID'
    AND p.createdAt BETWEEN :start AND :end
    """)
    Double getRentTotalSales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
    SELECT COALESCE(SUM(a.totalPrice), 0)
    FROM AdPayment a
    WHERE a.createdAt BETWEEN :start AND :end
    """)
    Double getAdTotalSales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
    SELECT COUNT(p)
    FROM Payment p
    WHERE p.status = 'PAID'
    AND p.createdAt BETWEEN :start AND :end
    """)
    Long getRentTotalCount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
    SELECT COUNT(a)
    FROM AdPayment a
    WHERE a.createdAt BETWEEN :start AND :end
    """)
    Long getAdTotalCount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
    SELECT COALESCE(SUM(p.adminPrice), 0)
    FROM Payment p
    WHERE p.status = 'PAID'
    AND p.createdAt BETWEEN :start AND :end
    """)
    Double getAdminProfit(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
    SELECT COALESCE(SUM(p.totalPrice), 0)
    FROM Payment p
    WHERE p.status = 'CANCELLED'
    AND p.createdAt BETWEEN :start AND :end
    """)
    Double getCancelledSales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
    SELECT COUNT(p)
    FROM Payment p
    WHERE p.status = 'CANCELLED'
    AND p.createdAt BETWEEN :start AND :end
    """)
    Long getCancelledCount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(b) FROM BannerActiveList b WHERE b.uploadDate BETWEEN :start AND :end")
    Long countBanner(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(bs) FROM Boost bs WHERE bs.endDate BETWEEN :start AND :end")
    Long countBoost(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
    SELECT COUNT(bw)
    FROM BannerWaitingList bw
    WHERE bw.createdAt BETWEEN :start AND :end
    """)
    Long getBannerWaitingCount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p FROM Payment p JOIN p.reservations r WHERE r.user.userId = :userId ORDER BY p.createdAt DESC")
    List<Payment> findTop5ByUserIdInReservations(@Param("userId") Long userId);


    // ===== 렌탈관리자 대시보드를 위한 쿼리 =====

    @Query("""
    SELECT COALESCE(SUM(p.totalPrice), 0)
    FROM Payment p
    JOIN p.reservations r
    WHERE r.rent.user.userId = :userId
      AND p.createdAt BETWEEN :start AND :end
      AND p.status = 'PAID'
    """)
    Double getTotalPaidPriceByUser(@Param("userId") Long userId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    @Query("""
    SELECT COUNT(p)
    FROM Payment p
    JOIN p.reservations r
    WHERE r.rent.user.userId = :userId
      AND p.createdAt BETWEEN :start AND :end
      AND p.status = 'PAID'
    """)
    Long getPaidCountByUser(@Param("userId") Long userId,
                            @Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end);

    @Query("""
    SELECT COALESCE(SUM(p.rentPrice), 0)
    FROM Payment p
    JOIN p.reservations r
    WHERE r.rent.user.userId = :userId
      AND p.createdAt BETWEEN :start AND :end
      AND p.status = 'PAID'
    """)
    Double getRentProfitByUser(@Param("userId") Long userId,
                               @Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);

    @Query("""
    SELECT COALESCE(SUM(p.totalPrice), 0)
    FROM Payment p
    JOIN p.reservations r
    WHERE r.rent.user.userId = :userId
      AND p.createdAt BETWEEN :start AND :end
      AND p.status = 'CANCELLED'
    """)
    Double getCancelledSalesByUser(@Param("userId") Long userId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    @Query("""
    SELECT COUNT(p)
    FROM Payment p
    JOIN p.reservations r
    WHERE r.rent.user.userId = :userId
      AND p.createdAt BETWEEN :start AND :end
      AND p.status = 'CANCELLED'
    """)
    Long getCancelledCountByUser(@Param("userId") Long userId,
                                 @Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);

    // ===== 렌탈샵 단일 대시보드를 위한 쿼리 =====

    @Query("""
    SELECT COALESCE(SUM(p.totalPrice), 0)
    FROM Payment p
    JOIN p.reservations r
    WHERE r.rent.user.userId = :userId
      AND r.rent.rentId = :rentId
      AND p.createdAt BETWEEN :start AND :end
      AND p.status = 'PAID'
    """)
    Double getTotalPaidPriceByRent(@Param("userId") Long userId,
                                   @Param("rentId") Long rentId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    @Query("""
    SELECT COUNT(p)
    FROM Payment p
    JOIN p.reservations r
    WHERE r.rent.user.userId = :userId
      AND r.rent.rentId = :rentId
      AND p.createdAt BETWEEN :start AND :end
      AND p.status = 'PAID'
    """)
    Long getPaidCountByRent(@Param("userId") Long userId,
                             @Param("rentId") Long rentId,
                             @Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end);

    @Query("""
    SELECT COALESCE(SUM(p.rentPrice), 0)
    FROM Payment p
    JOIN p.reservations r
    WHERE r.rent.user.userId = :userId
      AND r.rent.rentId = :rentId
      AND p.createdAt BETWEEN :start AND :end
      AND p.status = 'PAID'
    """)
    Double getRentProfitByRent(@Param("userId") Long userId,
                               @Param("rentId") Long rentId,
                               @Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);

    @Query("""
    SELECT COALESCE(SUM(p.totalPrice), 0)
    FROM Payment p
    JOIN p.reservations r
    WHERE r.rent.user.userId = :userId
      AND r.rent.rentId = :rentId
      AND p.createdAt BETWEEN :start AND :end
      AND p.status = 'CANCELLED'
    """)
    Double getCancelledSalesByRent(@Param("userId") Long userId,
                                   @Param("rentId") Long rentId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    @Query("""
    SELECT COUNT(p)
    FROM Payment p
    JOIN p.reservations r
    WHERE r.rent.user.userId = :userId
      AND r.rent.rentId = :rentId
      AND p.createdAt BETWEEN :start AND :end
      AND p.status = 'CANCELLED'
    """)
    Long getCancelledCountByRent(@Param("userId") Long userId,
                                 @Param("rentId") Long rentId,
                                 @Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);
}
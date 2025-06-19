package com.example.skip.service;

import com.example.skip.enumeration.ItemCategory;
import com.example.skip.enumeration.YesNo;
import com.example.skip.repository.PaymentRepository;
import com.example.skip.repository.reservation.ReservationItemRepository;
import com.example.skip.repository.reservation.ReservationRepository;
import com.example.skip.repository.RentRepository;
import com.example.skip.entity.Rent;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RentDashboardService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReservationItemRepository reservationItemRepository;
    @Autowired
    private RentRepository rentRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    public Map<String, Object> getSummary(Long userId, Long rentId, LocalDate start, LocalDate end) {
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to = end.plusDays(1).atStartOfDay();

        double totalSales;
        long totalSalesCount;
        double profit;
        long successCount;
        double cancelPrice;
        long cancelCount;

        if (rentId != null) {
            totalSales = Optional.ofNullable(paymentRepository.getTotalPaidPriceByRent(userId, rentId, from, to)).orElse(0.0)
                    + Optional.ofNullable(paymentRepository.getCancelledSalesByRent(userId, rentId, from, to)).orElse(0.0);
            totalSalesCount = Optional.ofNullable(paymentRepository.getPaidCountByRent(userId, rentId, from, to)).orElse(0L)
                    + Optional.ofNullable(paymentRepository.getCancelledCountByRent(userId, rentId, from, to)).orElse(0L);
            profit = Optional.ofNullable(paymentRepository.getRentProfitByRent(userId, rentId, from, to)).orElse(0.0);
            successCount = Optional.ofNullable(paymentRepository.getPaidCountByRent(userId, rentId, from, to)).orElse(0L);
            cancelPrice = Optional.ofNullable(paymentRepository.getCancelledSalesByRent(userId, rentId, from, to)).orElse(0.0);
            cancelCount = Optional.ofNullable(paymentRepository.getCancelledCountByRent(userId, rentId, from, to)).orElse(0L);
        } else {
            totalSales = Optional.ofNullable(paymentRepository.getTotalPaidPriceByUser(userId, from, to)).orElse(0.0)
                    + Optional.ofNullable(paymentRepository.getCancelledSalesByUser(userId, from, to)).orElse(0.0);
            totalSalesCount = Optional.ofNullable(paymentRepository.getPaidCountByUser(userId, from, to)).orElse(0L)
                    + Optional.ofNullable(paymentRepository.getCancelledCountByUser(userId, from, to)).orElse(0L);
            profit = Optional.ofNullable(paymentRepository.getRentProfitByUser(userId, from, to)).orElse(0.0);
            successCount = Optional.ofNullable(paymentRepository.getPaidCountByUser(userId, from, to)).orElse(0L);
            cancelPrice = Optional.ofNullable(paymentRepository.getCancelledSalesByUser(userId, from, to)).orElse(0.0);
            cancelCount = Optional.ofNullable(paymentRepository.getCancelledCountByUser(userId, from, to)).orElse(0L);
        }

        int totalAdCash;
        if (rentId != null) {
            totalAdCash = rentRepository.findById(rentId)
                    .map(Rent::getRemainAdCash)
                    .orElse(0);
        } else {
            totalAdCash = rentRepository
                    .findByUser_UserIdAndUseYn(userId, YesNo.Y, Sort.unsorted())
                    .stream()
                    .mapToInt(Rent::getRemainAdCash)
                    .sum();
        }

        long reservationCount = rentId != null ?
                reservationRepository.countByRent_RentId(rentId) :
                reservationRepository.countByRent_User_UserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("totalSales", totalSales);
        result.put("totalSalesCount", totalSalesCount);
        result.put("totalProfit", profit);
        result.put("totalSuccessCount", successCount);
        result.put("totalCancelPrice", cancelPrice);
        result.put("totalCancelCount", cancelCount);
        result.put("totalAdCash", totalAdCash);
        result.put("reservationCount", reservationCount);
        return result;
    }

    public List<Map<String, Object>> getSalesChartData(Long userId, Long rentId, LocalDate start, LocalDate end) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ItemCategory category : ItemCategory.values()) {
            long count = (rentId != null)
                    ? reservationItemRepository.countPaymentsByUserAndItemCategoryAndRent(
                        userId,
                        rentId,
                        category,
                        start.atStartOfDay(),
                        end.plusDays(1).atStartOfDay())
                    : reservationItemRepository.countPaymentsByUserAndItemCategory(
                        userId,
                        category,
                        start.atStartOfDay(),
                        end.plusDays(1).atStartOfDay()
                    );
            Map<String, Object> map = new HashMap<>();
            map.put("category", category.toString());
            map.put("count", count);
            result.add(map);
        }
        return result;
    }
}
package com.restroly.qrmenu.admin.dashboard.service;


import com.restroly.qrmenu.admin.dashboard.dto.DashboardStatDTO;
import com.restroly.qrmenu.admin.dashboard.dto.RevenueTrendDTO;
import com.restroly.qrmenu.common.enums.OrderStatus;
import com.restroly.qrmenu.order.entity.Order;
import com.restroly.qrmenu.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;

    @Override
    public List<DashboardStatDTO> getDashboardStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        BigDecimal todayRevenue = orderRepository.getTodayRevenue(startOfDay, endOfDay);

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String formattedRevenue = formatter.format(todayRevenue);

        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.PENDING,
                OrderStatus.CONFIRMED,
                OrderStatus.PREPARING
        );

        long liveOrders = orderRepository.countByStatusIn(activeStatuses);
        long todayOrders = orderRepository.countByCreatedAtBetween(startOfDay, endOfDay);
        long completedToday = orderRepository.countByStatusInAndCreatedAtBetween(
                List.of(OrderStatus.COMPLETED, OrderStatus.SERVED),
                startOfDay,
                endOfDay
        );
        double completionProgress = todayOrders == 0
                ? 0
                : BigDecimal.valueOf(completedToday * 100.0 / todayOrders)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        return List.of(
                new DashboardStatDTO(
                        "Today's Revenue",
                        formattedRevenue,
                        null,
                        null,
                        null,
                        "revenue",
                        "green",
                        null,
                        null
                ),
                new DashboardStatDTO(
                        "Live Orders",
                        String.valueOf(liveOrders),
                        null,
                        null,
                        "active",
                        "orders",
                        "orange",
                        true,
                        null
                ),
                new DashboardStatDTO(
                        "Today's Orders",
                        String.valueOf(todayOrders),
                        null,
                        null,
                        "total",
                        "orders",
                        "blue",
                        false,
                        null
                ),
                new DashboardStatDTO(
                        "Completion Rate",
                        completionProgress + "%",
                        null,
                        null,
                        "(" + completedToday + "/" + todayOrders + ")",
                        "payments",
                        "purple",
                        false,
                        completionProgress
                )
        );
    }

    @Override
    public List<RevenueTrendDTO> getRevenueTrend(int days) {
        int safeDays = Math.max(1, Math.min(days, 90));
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(safeDays - 1L);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        Map<LocalDate, List<Order>> ordersByDate = orderRepository.findByCreatedAtBetween(start, end)
                .stream()
                .filter(order -> order.getCreatedAt() != null)
                .collect(Collectors.groupingBy(order -> order.getCreatedAt().toLocalDate()));

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("d MMM");
        List<RevenueTrendDTO> trend = new ArrayList<>();

        for (int index = 0; index < safeDays; index++) {
            LocalDate date = startDate.plusDays(index);
            List<Order> orders = ordersByDate.getOrDefault(date, List.of());
            BigDecimal revenue = orders.stream()
                    .map(Order::getTotalAmount)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            trend.add(new RevenueTrendDTO(
                    date.format(dayFormatter),
                    revenue,
                    (long) orders.size()
            ));
        }

        return trend;
    }

}

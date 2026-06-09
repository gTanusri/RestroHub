package com.restroly.qrmenu.admin.dashboard.service;

import com.restroly.qrmenu.admin.dashboard.dto.DashboardStatDTO;
import com.restroly.qrmenu.admin.dashboard.dto.RevenueTrendDTO;

import java.util.List;

public interface DashboardService {

    List<DashboardStatDTO> getDashboardStats();

    List<RevenueTrendDTO> getRevenueTrend(int days);
}

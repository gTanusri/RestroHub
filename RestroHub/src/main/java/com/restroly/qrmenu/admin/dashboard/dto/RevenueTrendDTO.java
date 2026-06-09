package com.restroly.qrmenu.admin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueTrendDTO {

    private String day;
    private BigDecimal revenue;
    private Long orders;
}

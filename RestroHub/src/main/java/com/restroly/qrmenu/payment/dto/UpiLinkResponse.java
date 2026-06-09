package com.restroly.qrmenu.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UpiLinkResponse {

    private Long id;
    private String name;
    private String upiId;
    private Boolean defaultLink;
    private Long transactions;
    private BigDecimal revenue;
    private String paymentUrl;
}

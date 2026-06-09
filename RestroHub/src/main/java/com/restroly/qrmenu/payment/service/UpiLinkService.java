package com.restroly.qrmenu.payment.service;

import com.restroly.qrmenu.payment.dto.UpiLinkRequest;
import com.restroly.qrmenu.payment.dto.UpiLinkResponse;
import com.restroly.qrmenu.payment.dto.UpiPaymentLinkRequest;

import java.util.List;

public interface UpiLinkService {

    List<UpiLinkResponse> getAllLinks();

    UpiLinkResponse createLink(UpiLinkRequest request);

    UpiLinkResponse setDefault(Long id);

    void deleteLink(Long id);

    UpiLinkResponse generatePaymentLink(Long id, UpiPaymentLinkRequest request);
}

package com.restroly.qrmenu.payment.service;

import com.restroly.qrmenu.common.exception.ResourceAlreadyExistsException;
import com.restroly.qrmenu.common.exception.ResourceNotFoundException;
import com.restroly.qrmenu.payment.dto.UpiLinkRequest;
import com.restroly.qrmenu.payment.dto.UpiLinkResponse;
import com.restroly.qrmenu.payment.dto.UpiPaymentLinkRequest;
import com.restroly.qrmenu.payment.entity.UpiLink;
import com.restroly.qrmenu.payment.repository.UpiLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpiLinkServiceImpl implements UpiLinkService {

    private static final String DEFAULT_PAYEE_NAME = "Restroly";

    private final UpiLinkRepository upiLinkRepository;

    @Override
    public List<UpiLinkResponse> getAllLinks() {
        return upiLinkRepository.findAllByOrderByDefaultLinkDescCreatedAtAsc()
                .stream()
                .map(link -> toResponse(link, BigDecimal.ONE, "Test payment"))
                .toList();
    }

    @Override
    @Transactional
    public UpiLinkResponse createLink(UpiLinkRequest request) {
        String normalizedUpiId = request.getUpiId().trim().toLowerCase();
        if (upiLinkRepository.existsByUpiIdIgnoreCase(normalizedUpiId)) {
            throw new ResourceAlreadyExistsException("UPI link already exists");
        }

        boolean shouldBeDefault = Boolean.TRUE.equals(request.getDefaultLink())
                || upiLinkRepository.count() == 0;
        if (shouldBeDefault) {
            clearDefaultLinks();
        }

        UpiLink upiLink = UpiLink.builder()
                .name(request.getName().trim())
                .upiId(normalizedUpiId)
                .defaultLink(shouldBeDefault)
                .build();

        return toResponse(upiLinkRepository.save(upiLink), BigDecimal.ONE, "Test payment");
    }

    @Override
    @Transactional
    public UpiLinkResponse setDefault(Long id) {
        UpiLink upiLink = findById(id);
        clearDefaultLinks();
        upiLink.setDefaultLink(true);
        return toResponse(upiLinkRepository.save(upiLink), BigDecimal.ONE, "Test payment");
    }

    @Override
    @Transactional
    public void deleteLink(Long id) {
        UpiLink upiLink = findById(id);
        upiLinkRepository.delete(upiLink);

        if (Boolean.TRUE.equals(upiLink.getDefaultLink())) {
            upiLinkRepository.findAllByOrderByDefaultLinkDescCreatedAtAsc()
                    .stream()
                    .findFirst()
                    .ifPresent(nextDefault -> {
                        nextDefault.setDefaultLink(true);
                        upiLinkRepository.save(nextDefault);
                    });
        }
    }

    @Override
    public UpiLinkResponse generatePaymentLink(Long id, UpiPaymentLinkRequest request) {
        UpiLink upiLink = findById(id);
        String note = request.getNote() == null || request.getNote().isBlank()
                ? "Restroly payment"
                : request.getNote().trim();
        return toResponse(upiLink, request.getAmount(), note);
    }

    private UpiLink findById(Long id) {
        return upiLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UPI link not found"));
    }

    private void clearDefaultLinks() {
        upiLinkRepository.findByDefaultLinkTrue().ifPresent(existingDefault -> {
            existingDefault.setDefaultLink(false);
            upiLinkRepository.save(existingDefault);
        });
    }

    private UpiLinkResponse toResponse(UpiLink upiLink, BigDecimal amount, String note) {
        return UpiLinkResponse.builder()
                .id(upiLink.getId())
                .name(upiLink.getName())
                .upiId(upiLink.getUpiId())
                .defaultLink(upiLink.getDefaultLink())
                .transactions(upiLink.getTransactions())
                .revenue(upiLink.getRevenue())
                .paymentUrl(buildPaymentUrl(upiLink, amount, note))
                .build();
    }

    private String buildPaymentUrl(UpiLink upiLink, BigDecimal amount, String note) {
        return UriComponentsBuilder.fromUriString("upi://pay")
                .queryParam("pa", upiLink.getUpiId())
                .queryParam("pn", encode(DEFAULT_PAYEE_NAME))
                .queryParam("am", amount)
                .queryParam("cu", "INR")
                .queryParam("tn", encode(note))
                .build(true)
                .toUriString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

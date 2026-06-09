package com.restroly.qrmenu.payment.controller;

import com.restroly.qrmenu.payment.dto.UpiLinkRequest;
import com.restroly.qrmenu.payment.dto.UpiLinkResponse;
import com.restroly.qrmenu.payment.dto.UpiPaymentLinkRequest;
import com.restroly.qrmenu.payment.service.UpiLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/secure/api/v1/upi-links")
@RequiredArgsConstructor
public class UpiLinkController {

    private final UpiLinkService upiLinkService;

    @GetMapping
    public List<UpiLinkResponse> getAllLinks() {
        return upiLinkService.getAllLinks();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UpiLinkResponse createLink(@Valid @RequestBody UpiLinkRequest request) {
        return upiLinkService.createLink(request);
    }

    @PutMapping("/{id}/default")
    public UpiLinkResponse setDefault(@PathVariable Long id) {
        return upiLinkService.setDefault(id);
    }

    @PostMapping("/{id}/payment-link")
    public UpiLinkResponse generatePaymentLink(
            @PathVariable Long id,
            @Valid @RequestBody UpiPaymentLinkRequest request
    ) {
        return upiLinkService.generatePaymentLink(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLink(@PathVariable Long id) {
        upiLinkService.deleteLink(id);
    }
}

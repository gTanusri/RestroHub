package com.restroly.qrmenu.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpiLinkRequest {

    @NotBlank(message = "Account name is required")
    private String name;

    @NotBlank(message = "UPI ID is required")
    @Pattern(
            regexp = "^[A-Za-z0-9._-]{2,256}@[A-Za-z]{2,64}$",
            message = "UPI ID must look like name@bank"
    )
    private String upiId;

    private Boolean defaultLink;
}

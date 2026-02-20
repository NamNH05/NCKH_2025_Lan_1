package vn.id.luannv.auth_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private Long id;
    
    @NotBlank(message = "Recipient name is required")
    @Size(min = 2, max = 100, message = "Recipient name must be between 2 and 100 characters")
    private String recipientName;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Invalid Vietnamese phone number")
    private String phone;
    
    @NotBlank(message = "Address line is required")
    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    private String addressLine;
    
    @NotBlank(message = "Ward is required")
    @Size(min = 2, max = 100, message = "Ward must be between 2 and 100 characters")
    private String ward;
    
    @NotBlank(message = "District is required")
    @Size(min = 2, max = 100, message = "District must be between 2 and 100 characters")
    private String district;
    
    @NotBlank(message = "Province is required")
    @Size(min = 2, max = 100, message = "Province must be between 2 and 100 characters")
    private String province;
    
    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[0-9]{5,6}$", message = "Postal code must be 5-6 digits")
    private String postalCode;
    
    private Boolean isDefault;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}

package vn.id.luannv.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.id.luannv.auth_service.dto.AddressDTO;
import vn.id.luannv.auth_service.entity.Address;
import vn.id.luannv.auth_service.entity.User;
import vn.id.luannv.auth_service.repository.AddressRepository;
import vn.id.luannv.auth_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<AddressDTO> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AddressDTO createAddress(Long userId, AddressDTO addressDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = Address.builder()
                .user(user)
                .recipientName(addressDTO.getRecipientName())
                .phone(addressDTO.getPhone())
                .addressLine(addressDTO.getAddressLine())
                .ward(addressDTO.getWard())
                .district(addressDTO.getDistrict())
                .province(addressDTO.getProvince())
                .postalCode(addressDTO.getPostalCode())
                .isDefault(addressDTO.getIsDefault() != null ? addressDTO.getIsDefault() : false)
                .notes(addressDTO.getNotes())
                .build();

        Address saved = addressRepository.save(address);
        return convertToDTO(saved);
    }

    public AddressDTO updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        address.setRecipientName(addressDTO.getRecipientName());
        address.setPhone(addressDTO.getPhone());
        address.setAddressLine(addressDTO.getAddressLine());
        address.setWard(addressDTO.getWard());
        address.setDistrict(addressDTO.getDistrict());
        address.setProvince(addressDTO.getProvince());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setIsDefault(addressDTO.getIsDefault());
        address.setNotes(addressDTO.getNotes());

        Address updated = addressRepository.save(address);
        return convertToDTO(updated);
    }

    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        addressRepository.delete(address);
    }

    private AddressDTO convertToDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .recipientName(address.getRecipientName())
                .phone(address.getPhone())
                .addressLine(address.getAddressLine())
                .ward(address.getWard())
                .district(address.getDistrict())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .isDefault(address.getIsDefault())
                .notes(address.getNotes())
                .build();
    }
}

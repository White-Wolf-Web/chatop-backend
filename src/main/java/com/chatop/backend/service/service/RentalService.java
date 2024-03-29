package com.chatop.backend.service.service;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDto;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface RentalService {
    void createRental(RentalRequestDto rentalRequestDTO, Principal principal);

    RentalDto getRentalById(Long id);

    List<RentalDto> getAllRentals();

    RentalDto updateRental(Long id, RentalRequestDto rentalRequestDTO) throws IOException;

    void deleteRental(Long id) throws IOException;
}


package com.ntt.prueba.auth.service;

import com.ntt.prueba.auth.dto.PhoneDTO;
import com.ntt.prueba.shared.BaseService;

public interface PhoneService
        extends BaseService<PhoneDTO.CreatePhoneDTO, PhoneDTO.UpdatePhoneDTO, PhoneDTO, PhoneDTO.FiltersPhoneDTO> {
}
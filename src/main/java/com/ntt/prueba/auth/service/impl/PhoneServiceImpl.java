package com.ntt.prueba.auth.service.impl;

import org.springframework.stereotype.Service;

import com.ntt.prueba.auth.entity.Phone;
import com.ntt.prueba.auth.repository.PhoneRepository;
import com.ntt.prueba.auth.service.PhoneService;
import com.ntt.prueba.auth.dto.PhoneDTO;
import com.ntt.prueba.auth.dto.PhoneDTO.CreatePhoneDTO;
import com.ntt.prueba.auth.dto.PhoneDTO.FiltersPhoneDTO;
import com.ntt.prueba.auth.dto.PhoneDTO.UpdatePhoneDTO;
import com.ntt.prueba.auth.mapper.PhoneMapper;
import com.ntt.prueba.shared.BaseServiceImpl;

@Service
public class PhoneServiceImpl extends
        BaseServiceImpl<Phone, CreatePhoneDTO, UpdatePhoneDTO, PhoneDTO, FiltersPhoneDTO>
        implements PhoneService {

    public PhoneServiceImpl(PhoneRepository repository,
            PhoneMapper mapper) {
        super(repository, mapper);

    }

}
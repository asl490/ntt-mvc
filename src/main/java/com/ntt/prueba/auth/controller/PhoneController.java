package com.ntt.prueba.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ntt.prueba.auth.service.PhoneService;
import com.ntt.prueba.auth.dto.PhoneDTO;
import com.ntt.prueba.shared.BaseController;

@RestController
@RequestMapping("/phone")
public class PhoneController
        extends BaseController<PhoneDTO.CreatePhoneDTO, PhoneDTO.UpdatePhoneDTO, PhoneDTO, PhoneDTO.FiltersPhoneDTO> {

    public PhoneController(PhoneService service) {
        super(service);
    }

}
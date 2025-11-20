package com.ntt.prueba.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ntt.prueba.auth.dto.PhoneDTO;
import com.ntt.prueba.auth.dto.PhoneDTO.CreatePhoneDTO;
import com.ntt.prueba.auth.dto.PhoneDTO.UpdatePhoneDTO;
import com.ntt.prueba.auth.entity.Phone;
import com.ntt.prueba.auth.entity.User;
import com.ntt.prueba.auth.mapper.PhoneMapper;
import com.ntt.prueba.auth.repository.PhoneRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PhoneServiceImpl Tests")
class PhoneServiceImplTest {

    @Mock
    private PhoneRepository phoneRepository;

    @Mock
    private PhoneMapper phoneMapper;

    @InjectMocks
    private PhoneServiceImpl phoneService;

    private Phone phone;
    private PhoneDTO phoneDTO;
    private CreatePhoneDTO createPhoneDTO;
    private UpdatePhoneDTO updatePhoneDTO;
    private User user;
    private UUID phoneId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        phoneId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .username("testuser")
                .name("Test User")
                .build();

        phone = Phone.builder()
                .id(phoneId)
                .number("123456789")
                .cityCode("11")
                .countryCode("57")
                .user(user)
                .build();

        phoneDTO = PhoneDTO.builder()
                .id(phoneId)
                .number("123456789")
                .cityCode("11")
                .countryCode("57")
                .user(userId)
                .build();

        createPhoneDTO = new CreatePhoneDTO();
        createPhoneDTO.setNumber("123456789");
        createPhoneDTO.setCityCode("11");
        createPhoneDTO.setCountryCode("57");
        createPhoneDTO.setUser(userId);

        updatePhoneDTO = new UpdatePhoneDTO();
        updatePhoneDTO.setNumber("987654321");
        updatePhoneDTO.setCityCode("1");
        updatePhoneDTO.setCountryCode("57");
    }

    @Test
    @DisplayName("Should create phone successfully")
    void testCreatePhone() {
        // Arrange
        when(phoneMapper.toEntity(any(CreatePhoneDTO.class))).thenReturn(phone);
        when(phoneRepository.save(any(Phone.class))).thenReturn(phone);
        when(phoneMapper.toDTO(any(Phone.class))).thenReturn(phoneDTO);

        // Act
        PhoneDTO result = phoneService.create(createPhoneDTO);

        // Assert
        assertNotNull(result);
        assertEquals(phoneDTO.getId(), result.getId());
        assertEquals(phoneDTO.getNumber(), result.getNumber());
        assertEquals(phoneDTO.getCityCode(), result.getCityCode());
        assertEquals(phoneDTO.getCountryCode(), result.getCountryCode());

        verify(phoneMapper).toEntity(createPhoneDTO);
        verify(phoneRepository).save(any(Phone.class));
        verify(phoneMapper).toDTO(phone);
    }

    @Test
    @DisplayName("Should find phone by id successfully")
    void testFindById() {
        // Arrange
        when(phoneRepository.findById(phoneId)).thenReturn(Optional.of(phone));
        when(phoneMapper.toDTO(any(Phone.class))).thenReturn(phoneDTO);

        // Act
        PhoneDTO result = phoneService.getById(phoneId.toString());

        // Assert
        assertNotNull(result);
        assertEquals(phoneDTO.getId(), result.getId());
        assertEquals(phoneDTO.getNumber(), result.getNumber());

        verify(phoneRepository).findById(phoneId);
        verify(phoneMapper).toDTO(phone);
    }

    @Test
    @DisplayName("Should update phone successfully")
    void testUpdatePhone() {
        // Arrange
        Phone updatedPhone = Phone.builder()
                .id(phoneId)
                .number("987654321")
                .cityCode("1")
                .countryCode("57")
                .user(user)
                .build();

        PhoneDTO updatedPhoneDTO = PhoneDTO.builder()
                .id(phoneId)
                .number("987654321")
                .cityCode("1")
                .countryCode("57")
                .user(userId)
                .build();

        when(phoneRepository.findById(phoneId)).thenReturn(Optional.of(phone));
        doNothing().when(phoneMapper).updateEntityFromDTO(any(UpdatePhoneDTO.class), any(Phone.class));
        when(phoneRepository.save(any(Phone.class))).thenReturn(updatedPhone);
        when(phoneMapper.toDTO(any(Phone.class))).thenReturn(updatedPhoneDTO);

        // Act
        PhoneDTO result = phoneService.update(phoneId.toString(), updatePhoneDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedPhoneDTO.getNumber(), result.getNumber());
        assertEquals(updatedPhoneDTO.getCityCode(), result.getCityCode());

        verify(phoneRepository).findById(phoneId);
        verify(phoneMapper).updateEntityFromDTO(updatePhoneDTO, phone);
        verify(phoneRepository).save(phone);
        verify(phoneMapper).toDTO(any(Phone.class));
    }

}

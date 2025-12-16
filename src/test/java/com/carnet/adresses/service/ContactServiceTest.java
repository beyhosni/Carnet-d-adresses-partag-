package com.carnet.adresses.service;

import com.carnet.adresses.entity.Contact;
import com.carnet.adresses.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository repository;

    @InjectMocks
    private ContactService service;

    private Contact contact;

    @BeforeEach
    void setUp() {
        contact = new Contact();
        contact.setId(1L);
        contact.setNom("Doe");
        contact.setPrenom("John");
        contact.setEmail("john@example.com");
    }

    @Test
    void shouldCreateContact() {
        when(repository.save(any(Contact.class))).thenReturn(contact);

        Contact created = service.createContact(new Contact());

        assertNotNull(created);
        assertEquals("Doe", created.getNom());
        verify(repository, times(1)).save(any(Contact.class));
    }

    @Test
    void shouldGetContactById() {
        when(repository.findById(1L)).thenReturn(Optional.of(contact));

        Contact found = service.getContact(1L);

        assertNotNull(found);
        assertEquals("Doe", found.getNom());
    }

    @Test
    void shouldThrowExceptionWhenContactNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getContact(1L));
    }

    @Test
    void shouldSearchContacts() {
        Page<Contact> page = new PageImpl<>(Collections.singletonList(contact));
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.search("Doe", pageable)).thenReturn(page);

        Page<Contact> result = service.getContacts("Doe", pageable);

        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).search("Doe", pageable);
    }
}

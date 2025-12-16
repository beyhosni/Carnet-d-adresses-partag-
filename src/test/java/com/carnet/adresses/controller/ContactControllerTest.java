package com.carnet.adresses.controller;

import com.carnet.adresses.AbstractIntegrationTest;
import com.carnet.adresses.entity.Contact;
import com.carnet.adresses.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ContactControllerTest extends AbstractIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ContactRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveContact() {
        Contact contact = new Contact();
        contact.setNom("Doe");
        contact.setPrenom("John");
        contact.setEmail("john.doe@example.com");

        ResponseEntity<Contact> createResponse = restTemplate.postForEntity("/api/contacts", contact, Contact.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody().getId()).isNotNull();

        ResponseEntity<Contact> getResponse = restTemplate
                .getForEntity("/api/contacts/" + createResponse.getBody().getId(), Contact.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getNom()).isEqualTo("Doe");
    }
}

package com.carnet.adresses.controller;

import com.carnet.adresses.entity.Contact;
import com.carnet.adresses.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService service;

    public ContactController(ContactService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Contact> getAllContacts(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.getContacts(q, pageable);
    }

    @GetMapping("/{id}")
    public Contact getContact(@PathVariable Long id) {
        return service.getContact(id);
    }

    @PostMapping
    public Contact createContact(@Valid @RequestBody Contact contact) {
        return service.createContact(contact);
    }

    @PutMapping("/{id}")
    public Contact updateContact(@PathVariable Long id, @Valid @RequestBody Contact contact) {
        return service.updateContact(id, contact);
    }

    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable Long id) {
        service.deleteContact(id);
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        service.updatePhoto(id, file);
    }

    @GetMapping(value = "/{id}/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        byte[] photo = service.getPhoto(id);
        if (photo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(photo);
    }
}

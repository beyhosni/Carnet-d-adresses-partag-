package com.carnet.adresses.service;

import com.carnet.adresses.entity.Contact;
import com.carnet.adresses.repository.ContactRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
public class ContactService {

    private final ContactRepository repository;

    public ContactService(ContactRepository repository) {
        this.repository = repository;
    }

    public Page<Contact> getContacts(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return repository.findAll(pageable);
        }
        return repository.search(query, pageable);
    }

    public Contact getContact(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    public Contact createContact(Contact contact) {
        return repository.save(contact);
    }

    public Contact updateContact(Long id, Contact details) {
        Contact contact = getContact(id);
        contact.setNom(details.getNom());
        contact.setPrenom(details.getPrenom());
        contact.setEmail(details.getEmail());
        contact.setTelephone(details.getTelephone());
        contact.setTag(details.getTag());
        // Photo is handled separately or included?
        // If included in details (JSON), it might be null if not sent.
        // Assuming Photo API is separate as requested.
        return repository.save(contact);
    }

    public void deleteContact(Long id) {
        repository.deleteById(id);
    }

    public void updatePhoto(Long id, MultipartFile file) throws IOException {
        Contact contact = getContact(id);
        contact.setPhoto(file.getBytes());
        repository.save(contact);
    }

    public byte[] getPhoto(Long id) {
        Contact contact = getContact(id);
        return contact.getPhoto();
    }
}

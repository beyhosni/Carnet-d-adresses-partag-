package com.carnet.adresses.repository;

import com.carnet.adresses.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query("SELECT c FROM Contact c WHERE " +
            "LOWER(c.nom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.prenom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.tag) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Contact> search(@Param("query") String query, Pageable pageable);
}

package com.ikonicit.resource.tracker.repository;

import com.ikonicit.resource.tracker.entity.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, Integer> {
    Credentials findByEmailAndPassword(String email, String password);

    Credentials findByEmail(String email);
}

package be.intec.kazernemediaplayer.repository;

import be.intec.kazernemediaplayer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    void deleteById(Long userId); // Method to delete user by ID

    boolean existsByUsername(String username);
}
package be.intec.kazernemediaplayer.repository;

import be.intec.kazernemediaplayer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    void deleteById(Long userId); // Method to delete user by ID
}
package be.intec.kazernemediaplayer.repository;

import  be.intec.kazernemediaplayer.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryRepository extends JpaRepository<Library, Long> {
    Optional<Library> findByUserId(Long id); // If you meant to find by library ID

    // Or
    List<Library> findAllByUserId(Long userId); // If you meant to find by user ID

    List<Library> findByUserIdNot(Long userId);
    Optional<Library> findFirstByUserId(Long userId);

}
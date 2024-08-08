package be.intec.kazernemediaplayer.repository;

import  be.intec.kazernemediaplayer.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryRepository extends JpaRepository<Library, Long> {
    List<Library> findAllByOwnerId(Long ownerId);
    Optional<Library> findById(Long libraryId);

    List<Library> saveLibrary (Library library);

}
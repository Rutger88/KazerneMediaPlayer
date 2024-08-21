package be.intec.kazernemediaplayer.repository;

import be.intec.kazernemediaplayer.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface MediaRepository extends JpaRepository<MediaFile, Long> {
    Optional<MediaFile> findById(Long id);
    Optional<MediaFile> findFirstByIdGreaterThanOrderByIdAsc(Long currentId);
    Optional<MediaFile> findFirstByIdLessThanOrderByIdDesc(Long currentId);
}
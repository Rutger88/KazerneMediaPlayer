package be.intec.kazernemediaplayer.repository;

import be.intec.kazernemediaplayer.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<MediaFile, Long> {

    Optional<MediaFile> findById(Long id);
    Optional<MediaFile> findByIdAndLibraryId(Long mediaId, Long libraryId);
    Optional<MediaFile> findFirstByIdGreaterThanOrderByIdAsc(Long currentId);

    Optional<MediaFile> findFirstByIdLessThanOrderByIdDesc(Long currentId);

    Optional<MediaFile> findFirstByOrderByIdAsc();


    Optional<MediaFile> findFirstByIdGreaterThanAndLibraryIdOrderByIdAsc(Long currentId, Long libraryId);

    Optional<MediaFile> findFirstByIdLessThanAndLibraryIdOrderByIdDesc(Long currentId, Long libraryId);

    Optional<MediaFile> findFirstByLibraryIdOrderByIdDesc(Long libraryId);
    Optional<MediaFile> findFirstByOrderByIdDesc();

    // Updated method to return an Optional<MediaFile>
    Optional<MediaFile> findByIsPlayingTrue();
}

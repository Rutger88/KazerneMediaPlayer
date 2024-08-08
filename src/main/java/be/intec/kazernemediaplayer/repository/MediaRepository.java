package be.intec.kazernemediaplayer.repository;

import be.intec.kazernemediaplayer.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface MediaRepository extends JpaRepository<MediaFile, Long> {
}
package be.intec.kazernemediaplayer.service;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class MovieService {

    @Autowired
    private MediaRepository mediaRepository;

    private MediaFile currentlyPlayingMovie;

    public MediaFile playMovie(Long mediaId) {
        currentlyPlayingMovie = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + mediaId));
        currentlyPlayingMovie.setIsPlaying(true);
        return currentlyPlayingMovie;
    }

    public void stopMovie() {
        if (currentlyPlayingMovie != null) {
            currentlyPlayingMovie.setIsPlaying(false);
            currentlyPlayingMovie = null;
        }
    }

    public MediaFile playNextMovie(Long currentId) {
        return mediaRepository.findFirstByIdGreaterThanOrderByIdAsc(currentId)
                .orElseThrow(() -> new RuntimeException("No more movies available after ID " + currentId));
    }

    public MediaFile playPreviousMovie(Long currentId) {
        return mediaRepository.findFirstByIdLessThanOrderByIdDesc(currentId)
                .orElseThrow(() -> new RuntimeException("No previous movie found before ID " + currentId));
    }


    public ResponseEntity<Resource> streamMovie(Long mediaId) {
        try {
            // Fetch the MediaFile object from the database
            Optional<MediaFile> mediaFileOptional = mediaRepository.findById(mediaId);

            if (mediaFileOptional.isEmpty()) {
                throw new RuntimeException("Movie not found with ID: " + mediaId);
            }

            MediaFile mediaFile = mediaFileOptional.get();

            // Ensure the file path is not null
            String filePath = mediaFile.getUrl();
            if (filePath == null) {
                throw new RuntimeException("File path is null for movie with ID: " + mediaId);
            }

            // Create a Path object
            Path path = Paths.get(filePath);

            // Check if the file exists
            if (!Files.exists(path)) {
                throw new RuntimeException("File not found: " + filePath);
            }

            // Load the file as a resource
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Could not read the file: " + filePath);
            }

            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Error streaming movie with ID: " + mediaId, e);
        }
    }

    private String getFileNameFromMediaId(Long mediaId) throws IOException {
        Path mediaDirectory = Paths.get("D:/KazerneMediaPlayer Songs 2024/");

        try (Stream<Path> paths = Files.walk(mediaDirectory)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.contains(mediaId.toString()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("File not found for mediaId: " + mediaId));
        }
    }
}

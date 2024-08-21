package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.repository.MediaRepository;
import be.intec.kazernemediaplayer.service.MediaNotFoundException;
import be.intec.kazernemediaplayer.service.StreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/stream")
public class StreamingController {
    @Autowired
    private StreamingService streamingService;
    @Autowired
    private MediaRepository mediaRepository;
    @GetMapping("/stream/{mediaId}")
    public ResponseEntity<Resource> streamMedia(@PathVariable Long mediaId) {
        try {
            // Fetch the media file path based on the media ID from the database
            MediaFile mediaFile = mediaRepository.findById(mediaId)
                    .orElseThrow(() -> new RuntimeException("Media file not found"));

            Path mediaPath = Paths.get(mediaFile.getPath());
            Resource mediaResource = new UrlResource(mediaPath.toUri());

            if (mediaResource.exists() || mediaResource.isReadable()) {
                String mediaType = Files.probeContentType(mediaPath);

                if (mediaType == null) {
                    mediaType = "application/octet-stream"; // Fallback to binary if type cannot be determined
                }

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, mediaType);

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(mediaResource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading the file", e);
        }
    }

    /* @ExceptionHandler(MediaNotFoundException.class)
     public ResponseEntity<String> handleMediaNotFoundException(MediaNotFoundException ex) {
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
     }
    }
     */
    @ExceptionHandler(MediaNotFoundException.class)
    public ResponseEntity<String> handleMediaNotFoundException(MediaNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}

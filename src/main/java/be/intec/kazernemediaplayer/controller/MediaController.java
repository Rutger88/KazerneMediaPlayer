package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.service.MediaNotFoundException;
import be.intec.kazernemediaplayer.service.MediaService;
import be.intec.kazernemediaplayer.service.StreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/media")
public class MediaController {

    private static final Logger logger = LoggerFactory.getLogger(MediaController.class);
    private final MediaService mediaService;
    private final StreamingService streamingService;

    @Autowired
    public MediaController(MediaService mediaService, StreamingService streamingService) {
        this.mediaService = mediaService;
        this.streamingService = streamingService;
    }

    @GetMapping
    public ResponseEntity<List<MediaFile>> getAllMedia() {
        List<MediaFile> mediaFiles = mediaService.findAll();
        return ResponseEntity.ok(mediaFiles);
    }

    @PostMapping("/upload")
    public ResponseEntity<MediaFile> uploadMedia(@RequestParam("file") MultipartFile file, @RequestParam("libraryId") Long libraryId) throws IOException {
        logger.info("Received file upload request for library ID: {}", libraryId);
        MediaFile uploadedMediaFile = mediaService.uploadMedia(file, libraryId);
        logger.info("File uploaded successfully with name: {}", uploadedMediaFile.getName());
        return ResponseEntity.ok(uploadedMediaFile);
    }

    @GetMapping(value = "/play/{currentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MediaFile> playMedia(@PathVariable Long currentId) {
        logger.debug("Received request to play media with id: {}", currentId);
        try {
            MediaFile mediaFile = mediaService.playMedia(currentId);
            if (mediaFile == null) {
                logger.error("Media file not found with id: {}", currentId);
                return ResponseEntity.notFound().build();
            }
            logger.debug("Returning media file: {}", mediaFile);
            return ResponseEntity.ok(mediaFile);
        } catch (MediaNotFoundException ex) {
            logger.error("Media file not found with id: {}", currentId, ex);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.error("An unexpected error occurred while trying to play media with id: {}", currentId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/stop")
    public ResponseEntity<Void> stopMedia() {
        logger.info("Received request to stop media playback.");
        mediaService.stopMedia();
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/next/{currentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MediaFile> playNext(@PathVariable Long currentId) {
        logger.info("Attempting to fetch next media file after currentId: {}", currentId);
        try {
            MediaFile nextMediaFile = mediaService.playNext(currentId);
            logger.info("Fetched next media file with ID: {} after currentId: {}", nextMediaFile.getId(), currentId);
            return ResponseEntity.ok(nextMediaFile);
        } catch (MediaNotFoundException ex) {
            logger.error("Next media file not found after id: {}", currentId, ex);
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/previous/{currentId}")
    public ResponseEntity<MediaFile> playPrevious(@PathVariable Long currentId) {
        logger.info("Received request to play previous media before id: {}", currentId);
        MediaFile previousMediaFile = mediaService.playPrevious(currentId);
        if (previousMediaFile == null) {
            logger.error("Previous media file not found before id: {}", currentId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(previousMediaFile);
    }


    @GetMapping("/stream/{mediaId}")
    public ResponseEntity<Resource> streamMedia(@PathVariable Long mediaId) {
        try {
            String fileName = getFileNameFromMediaId(mediaId);
            Path mediaPath = Paths.get("D:/KazerneMediaPlayer Songs 2024/", fileName);

            // Add logging to verify the constructed path
            System.out.println("Attempting to read file at path: " + mediaPath.toString());

            Resource mediaResource = new UrlResource(mediaPath.toUri());

            if (mediaResource.exists() && mediaResource.isReadable()) {
                String mediaType = Files.probeContentType(mediaPath);

                if (mediaType == null) {
                    mediaType = "application/octet-stream";
                }

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, mediaType);

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(mediaResource);
            } else {
                throw new RuntimeException("Could not read the file: " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading the file", e);
        }
    }


    // Example method to get the filename from the mediaId
    private String getFileNameFromMediaId(Long mediaId) {
        // Implement logic to fetch filename based on mediaId
        // For simplicity, assume mediaId maps directly to a filename
        // Example: if mediaId = 1724175884820, then filename might be "1724175884820_file.mp3"
        return mediaId + "_file.mp3"; // Simplified for example purposes
    }


    @DeleteMapping("/delete/{mediaId}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long mediaId) {
        try {
            mediaService.deleteMedia(mediaId);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
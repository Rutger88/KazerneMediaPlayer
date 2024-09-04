package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.service.MediaNotFoundException;
import be.intec.kazernemediaplayer.service.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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

    @Autowired
    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
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
            // Update URL to point to the streaming endpoint
            String mediaUrl = "/media/stream/" + mediaFile.getId();
            mediaFile.setUrl(mediaUrl);  // Make sure the correct URL is sent to the frontend
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
            nextMediaFile.setUrl("/media/stream/" + nextMediaFile.getId());  // Set the URL here
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
        // Update the URL for streaming
        previousMediaFile.setUrl("/media/stream/" + previousMediaFile.getId());
        return ResponseEntity.ok(previousMediaFile);
    }

    @GetMapping(value = "/stream/{mediaId}")
    public ResponseEntity<Resource> streamMedia(@PathVariable Long mediaId) {
        try {
            MediaFile mediaFile = mediaService.playMedia(mediaId);
            String filePath = mediaFile.getFilePath();

            if (filePath == null || filePath.isEmpty()) {
                logger.error("File path is null or empty for media ID: {}", mediaId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Path mediaPath = Paths.get(filePath);
            if (!Files.exists(mediaPath)) {
                logger.error("File not found: {}", filePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Resource mediaResource = new UrlResource(mediaPath.toUri());
            String mediaType = Files.probeContentType(mediaPath);
            if (mediaType == null) {
                mediaType = "application/octet-stream";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, mediaType);
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(mediaResource);
        } catch (Exception e) {
            logger.error("Error reading the file for media ID: {}", mediaId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
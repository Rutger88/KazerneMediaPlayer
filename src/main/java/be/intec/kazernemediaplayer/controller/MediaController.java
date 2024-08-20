package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.service.MediaNotFoundException;
import be.intec.kazernemediaplayer.service.MediaService;
import be.intec.kazernemediaplayer.service.StreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        MediaFile mediaFile = mediaService.playMedia(currentId);
        logger.debug("Returning media file: {}", mediaFile);
        return ResponseEntity.ok(mediaFile);
    }

    @PostMapping("/stop")
    public ResponseEntity<Void> stopMedia() {
        logger.info("Received request to stop media playback.");
        mediaService.stopMedia();
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/next/{currentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MediaFile> playNext(@PathVariable Long currentId) {
        logger.info("Received request to play next media after id: {}", currentId);
        MediaFile nextMediaFile = mediaService.playNext(currentId);
        if (nextMediaFile == null) {
            logger.error("Next media file not found after id: {}", currentId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(nextMediaFile);
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


    @GetMapping("/stream/{id}")
    public ResponseEntity<String> streamMedia(@PathVariable Long id) {
        logger.info("Received request to stream media with id: {}", id);
        try {
            String streamingUrl = streamingService.streamMedia(id);
            logger.info("Streaming media with id: {}", id);
            return ResponseEntity.ok(streamingUrl);
        } catch (MediaNotFoundException e) {
            logger.error("Media not found with id: {}", id);
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}

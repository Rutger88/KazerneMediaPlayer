package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.service.MediaNotFoundException;
import be.intec.kazernemediaplayer.service.MediaService;
import be.intec.kazernemediaplayer.service.StreamingService;
//import be.intec.kazernemediaplayer.service.StreamingServiceImpl;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/media")
public class MediaController {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(MediaController.class);
    private final MediaService mediaService;
    private final StreamingService streamingService;

    @Autowired
    public MediaController(MediaService mediaService, StreamingService streamingService) {
        this.mediaService = mediaService;
        this.streamingService = streamingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<MediaFile> uploadMedia(@RequestParam("file") MultipartFile file, @RequestParam("libraryId") Long libraryId) throws IOException {
        return ResponseEntity.ok(mediaService.uploadMedia(file, libraryId));
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
        mediaService.stopMedia();
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/next/{currentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MediaFile> playNext(@PathVariable Long currentId) {
        return ResponseEntity.ok(mediaService.playNext(currentId));
    }

    @GetMapping("/previous/{currentId}")
    public ResponseEntity<MediaFile> playPrevious(@PathVariable Long currentId) {
        return ResponseEntity.ok(mediaService.playPrevious(currentId));
    }

    @GetMapping("/stream/{id}")
    public ResponseEntity<String> streamMedia(@PathVariable Long id) {
        try {
            String streamingUrl = streamingService.streamMedia(id);
            return ResponseEntity.ok(streamingUrl);
        } catch (MediaNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}


package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.service.MediaNotFoundException;
import be.intec.kazernemediaplayer.service.StreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController
@RequestMapping("/stream")
public class StreamingController {
    @Autowired
    private StreamingService streamingService;

    @GetMapping("/{mediaId}")
    public ResponseEntity<Resource> streamMedia(@PathVariable Long mediaId) {
        try {
            Resource mediaResource = streamingService.getMediaFileResource(mediaId);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg"); // Assuming the media is an MP3 file

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(mediaResource);
        } catch (MediaNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found", e);
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

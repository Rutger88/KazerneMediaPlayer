package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.service.MediaNotFoundException;
import be.intec.kazernemediaplayer.service.StreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stream")
public class StreamingController {
    @Autowired
    private StreamingService streamingService;

    @GetMapping("/{mediaId}")
    public ResponseEntity<String> streamMedia(@PathVariable Long mediaId) {
        return ResponseEntity.ok(streamingService.streamMedia(mediaId));
    }
    @ExceptionHandler(MediaNotFoundException.class)
    public ResponseEntity<String> handleMediaNotFoundException(MediaNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}

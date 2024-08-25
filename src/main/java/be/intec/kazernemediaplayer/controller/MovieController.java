package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    @Autowired
    private MovieService movieService;

    @GetMapping("/play/{id}")
    public ResponseEntity<MediaFile> playMovie(@PathVariable Long id) {
        logger.debug("Received request to play movie with id: {}", id);
        try {
            MediaFile mediaFile = movieService.playMovie(id);
            return ResponseEntity.ok(mediaFile);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to play movie with id: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<Void> stopMovie() {
        logger.info("Received request to stop movie playback.");
        movieService.stopMovie();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/next/{id}")
    public ResponseEntity<MediaFile> playNextMovie(@PathVariable Long id) {
        logger.info("Attempting to fetch next movie after ID: {}", id);
        try {
            MediaFile nextMovie = movieService.playNextMovie(id);
            return ResponseEntity.ok(nextMovie);
        } catch (Exception ex) {
            logger.error("An error occurred while fetching next movie after ID: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/previous/{id}")
    public ResponseEntity<MediaFile> playPreviousMovie(@PathVariable Long id) {
        logger.info("Attempting to fetch previous movie before ID: {}", id);
        try {
            MediaFile previousMovie = movieService.playPreviousMovie(id);
            return ResponseEntity.ok(previousMovie);
        } catch (Exception ex) {
            logger.error("An error occurred while fetching previous movie before ID: {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stream/{mediaId}")
    public ResponseEntity<Resource> streamMovie(@PathVariable Long mediaId) {
        logger.info("Streaming movie with ID: {}", mediaId);
        try {
            return movieService.streamMovie(mediaId);
        } catch (Exception ex) {
            logger.error("An error occurred while streaming movie with ID: {}", mediaId, ex);
            return ResponseEntity.status(500).build();  // Internal Server Error
        }
    }
}

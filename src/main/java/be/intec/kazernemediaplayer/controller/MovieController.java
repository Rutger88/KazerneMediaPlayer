package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.service.MovieService;
import be.intec.kazernemediaplayer.service.MediaNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/play/{id}")
    public ResponseEntity<MediaFile> playMovie(@PathVariable Long id) {
        try {
            MediaFile mediaFile = movieService.playMovie(id);
            return ResponseEntity.ok(mediaFile);
        } catch (MediaNotFoundException e) {
            return ResponseEntity.notFound().build(); // Return 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Return 500 Internal Server Error
        }
    }

    @GetMapping("/currently-playing")
    public ResponseEntity<MediaFile> getCurrentlyPlayingMovie() {
        try {
            Optional<MediaFile> currentlyPlaying = movieService.getCurrentlyPlayingMovie();
            if (currentlyPlaying.isPresent() && currentlyPlaying.get().getIsPlaying()) {
                return ResponseEntity.ok(currentlyPlaying.get());
            }
            return ResponseEntity.noContent().build(); // Return 204 No Content if no movie is playing
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }


    @PostMapping("/stop")
    public ResponseEntity<Void> stopMovie() {
        try {
            movieService.stopMovie();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Return 500 Internal Server Error
        }
    }

    @GetMapping("/next/{id}")
    public ResponseEntity<MediaFile> playNextMovie(@PathVariable Long id) {
        try {
            MediaFile nextMovie = movieService.playNextMovie(id);
            return ResponseEntity.ok(nextMovie);
        } catch (MediaNotFoundException e) {
            return ResponseEntity.notFound().build(); // Return 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Return 500 Internal Server Error
        }
    }

    @GetMapping("/previous/{id}")
    public ResponseEntity<MediaFile> playPreviousMovie(@PathVariable Long id) {
        try {
            MediaFile previousMovie = movieService.playPreviousMovie(id);
            return ResponseEntity.ok(previousMovie);
        } catch (MediaNotFoundException e) {
            return ResponseEntity.notFound().build(); // Return 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Return 500 Internal Server Error
        }
    }
}

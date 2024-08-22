package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/{id}/play")
    public ResponseEntity<MediaFile> playMovie(@PathVariable Long id) {
        MediaFile mediaFile = movieService.playMovie(id);
        return ResponseEntity.ok(mediaFile);
    }

    @GetMapping("/currently-playing")
    public ResponseEntity<MediaFile> getCurrentlyPlayingMovie() {
        MediaFile currentlyPlaying = movieService.getCurrentlyPlayingMovie();
        return ResponseEntity.ok(currentlyPlaying);
    }

    @PostMapping("/stop")
    public ResponseEntity<Void> stopMovie() {
        movieService.stopMovie();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/next")
    public ResponseEntity<MediaFile> playNextMovie(@PathVariable Long id) {
        MediaFile nextMovie = movieService.playNextMovie(id);
        return ResponseEntity.ok(nextMovie);
    }

    @GetMapping("/{id}/previous")
    public ResponseEntity<MediaFile> playPreviousMovie(@PathVariable Long id) {
        MediaFile previousMovie = movieService.playPreviousMovie(id);
        return ResponseEntity.ok(previousMovie);
    }
}

package be.intec.kazernemediaplayer.service;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class MovieService {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MediaRepository mediaRepository;
    private MediaFile currentlyPlayingMovie;

    @Autowired
    public MovieService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public MediaFile playMovie(Long mediaId) {
        currentlyPlayingMovie = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException("Movie not found with ID: " + mediaId));
        logger.info("Playing movie with ID: {}", mediaId);
        return currentlyPlayingMovie;
    }

    public MediaFile getCurrentlyPlayingMovie() {
        return currentlyPlayingMovie;
    }

    public void stopMovie() {
        currentlyPlayingMovie = null;
        logger.info("Stopped playing movie.");
    }

    public MediaFile playNextMovie(Long currentId) {
        logger.info("Fetching next movie after ID: " + currentId);
        MediaFile nextMovie = mediaRepository.findFirstByIdGreaterThanOrderByIdAsc(currentId)
                .orElseThrow(() -> new MediaNotFoundException("No more movies available after ID " + currentId));
        logger.info("Next movie ID: " + nextMovie.getId());
        return nextMovie;
    }

    public MediaFile playPreviousMovie(Long currentId) {
        logger.info("Fetching previous movie before ID: " + currentId);
        Optional<MediaFile> previousMovie = mediaRepository.findFirstByIdLessThanOrderByIdDesc(currentId);

        if (previousMovie.isPresent()) {
            logger.info("Previous movie ID: {}", previousMovie.get().getId());
            return previousMovie.get();
        } else {
            logger.info("No previous movie found, returning the last movie.");
            return mediaRepository.findFirstByOrderByIdDesc()
                    .orElseThrow(() -> new MediaNotFoundException("No movies found in the library."));
        }
    }
}


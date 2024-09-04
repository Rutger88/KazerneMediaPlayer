package be.intec.kazernemediaplayer.service;

import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.repository.LibraryRepository;
import be.intec.kazernemediaplayer.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class MediaService {

    private static final Logger logger = LoggerFactory.getLogger(MediaService.class);

    @Value("${media.upload-dir}")
    private String directoryPath;

    private final MediaRepository mediaRepository;
    private final LibraryRepository libraryRepository;
    private MediaFile currentlyPlaying;

    @Autowired
    public MediaService(MediaRepository mediaRepository, LibraryRepository libraryRepository) {
        this.mediaRepository = mediaRepository;
        this.libraryRepository = libraryRepository;
    }

    public MediaFile uploadMedia(MultipartFile multipartFile, Long libraryId) throws IOException {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        Files.createDirectories(Paths.get(directoryPath));

        String originalFileName = Paths.get(multipartFile.getOriginalFilename()).getFileName().toString();
        String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
        String filePath = Paths.get(directoryPath, uniqueFileName).toString();

        File file = new File(filePath);

        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            logger.error("Failed to save file: " + uniqueFileName, e);
            throw e;
        }

        logger.info("Attempting to find library with ID: {}", libraryId);
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid library ID: " + libraryId));

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName(originalFileName);
        mediaFile.setUrl("/media/stream/" + uniqueFileName);  // Update this line to set the correct stream URL
        mediaFile.setFilePath(filePath);
        mediaFile.setType(detectMediaType(originalFileName));
        mediaFile.setLibrary(library);

        try {
            logger.info("Saving media file for library ID: {}", libraryId);
            return mediaRepository.save(mediaFile);
        } catch (Exception e) {
            Files.deleteIfExists(Paths.get(filePath)); // Rollback file save on database failure
            throw e;
        }
    }

    public void deleteMedia(Long mediaId) throws IOException {
        MediaFile mediaFile = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("Media file not found with ID: " + mediaId));

        // Delete the file from the filesystem
        File file = new File(mediaFile.getFilePath());  // Update this to file path
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Failed to delete file: " + mediaFile.getFilePath());
            }
        } else {
            logger.warn("File not found in filesystem: {}", mediaFile.getFilePath());
        }

        // Always delete the media file record from the database
        mediaRepository.deleteById(mediaId);
    }

    private String detectMediaType(String fileName) {
        try {
            return Files.probeContentType(Paths.get(fileName));
        } catch (IOException e) {
            logger.error("Unable to detect media type for file: {}", fileName, e);
            return "unknown";
        }
    }

    public MediaFile playMedia(Long mediaId) {
        MediaFile mediaFile = mediaRepository.findById(mediaId).orElse(null);
        if (mediaFile != null) {
            mediaFile.setUrl("/media/stream/" + mediaId);  // Set the correct URL for streaming
            currentlyPlaying = mediaFile;
        }
        return currentlyPlaying;
    }

    public MediaFile getCurrentlyPlaying() {
        return currentlyPlaying;
    }

    public void stopMedia() {
        currentlyPlaying = null;
    }

    public MediaFile playNext(Long currentId) {
        logger.info("Fetching next media file after ID: " + currentId);
        Optional<MediaFile> nextMediaOpt = mediaRepository.findFirstByIdGreaterThanOrderByIdAsc(currentId);

        if (nextMediaOpt.isPresent()) {
            MediaFile nextMedia = nextMediaOpt.get();
            logger.info("Next media ID: " + nextMedia.getId());
            currentlyPlaying = nextMedia;
            return nextMedia;
        } else {
            logger.info("No more media files available after ID: " + currentId);
            throw new MediaNotFoundException("No more media files available.");
        }
    }

    public MediaFile playPrevious(Long currentId) {
        logger.info("Fetching previous media file before ID: {}", currentId);
        Optional<MediaFile> previousMediaOpt = mediaRepository.findFirstByIdLessThanOrderByIdDesc(currentId);

        if (previousMediaOpt.isPresent()) {
            MediaFile previousMedia = previousMediaOpt.get();
            logger.info("Previous media ID: {}", previousMedia.getId());
            previousMedia.setUrl("/media/stream/" + previousMedia.getId());  // Ensure correct URL
            currentlyPlaying = previousMedia;
            return previousMedia;
        } else {
            logger.info("No previous media found, fetching the last media file.");
            currentlyPlaying = mediaRepository.findFirstByOrderByIdDesc()
                    .orElseThrow(() -> new MediaNotFoundException("No media files found."));
            return currentlyPlaying;
        }
    }

    public List<MediaFile> findAll() {
        return mediaRepository.findAll();
    }
}

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
        multipartFile.transferTo(file);

        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid library ID"));

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName(originalFileName);
        mediaFile.setUrl(filePath); // Make sure this is set
        mediaFile.setType(detectMediaType(originalFileName));
        mediaFile.setLibrary(library);
        mediaFile.setFilePath(filePath); // Ensure this is set

        return mediaRepository.save(mediaFile);
    }

    public void deleteMedia(Long mediaId) throws IOException {
        MediaFile mediaFile = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("Media file not found with ID: " + mediaId));

        // Delete the file from the filesystem
        File file = new File(mediaFile.getUrl());
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Failed to delete file: " + mediaFile.getUrl());
            }
        } else {
            logger.warn("File not found in filesystem: {}", mediaFile.getUrl());
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
        currentlyPlaying = mediaRepository.findById(mediaId).orElse(null);
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

        // Fetch the next media file with a valid filePath
        Optional<MediaFile> nextMediaOpt = mediaRepository.findFirstByIdGreaterThanOrderByIdAsc(currentId);

        while (nextMediaOpt.isPresent()) {
            MediaFile nextMedia = nextMediaOpt.get();

            // Check if the file path is valid
            if (nextMedia.getFilePath() != null && !nextMedia.getFilePath().isEmpty()) {
                logger.info("Next media ID: " + nextMedia.getId());

                // Update the currently playing media file
                currentlyPlaying = nextMedia;
                return nextMedia;
            } else {
                logger.warn("Next media file has no valid file path, skipping to next available.");
                // Fetch the next media file
                nextMediaOpt = mediaRepository.findFirstByIdGreaterThanOrderByIdAsc(nextMedia.getId());
            }
        }

        throw new MediaNotFoundException("No more media files available after ID " + currentId);
    }

    public MediaFile playPrevious(Long currentId) {
        logger.info("Fetching previous media file before ID: {}", currentId);

        // Fetch the previous media file with a valid filePath
        Optional<MediaFile> previousMediaOpt = mediaRepository.findFirstByIdLessThanOrderByIdDesc(currentId);

        while (previousMediaOpt.isPresent()) {
            MediaFile previousMedia = previousMediaOpt.get();

            // Check if the file path is valid
            if (previousMedia.getFilePath() != null && !previousMedia.getFilePath().isEmpty()) {
                logger.info("Previous media ID: {}", previousMedia.getId());

                // Update the currently playing media file
                currentlyPlaying = previousMedia;
                return previousMedia;
            } else {
                logger.warn("Previous media file has no valid file path, skipping to previous available.");
                // Fetch the previous media file
                previousMediaOpt = mediaRepository.findFirstByIdLessThanOrderByIdDesc(previousMedia.getId());
            }
        }

        logger.info("No previous media found, returning the last media file.");
        currentlyPlaying = mediaRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new MediaNotFoundException("No media files found in the library."));
        return currentlyPlaying;
    }


    public List<MediaFile> findAll() {
        return mediaRepository.findAll();
    }
}

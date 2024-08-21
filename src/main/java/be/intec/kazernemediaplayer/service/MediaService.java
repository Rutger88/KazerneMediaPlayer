package be.intec.kazernemediaplayer.service;

import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.repository.LibraryRepository;
import be.intec.kazernemediaplayer.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        // Ensure the directory exists
        Files.createDirectories(Paths.get(directoryPath));

        // Sanitize file name and handle potential filename collisions
        String originalFileName = Paths.get(multipartFile.getOriginalFilename()).getFileName().toString();
        String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
        String filePath = Paths.get(directoryPath, uniqueFileName).toString();

        // Save the file to the specified path
        File file = new File(filePath);
        multipartFile.transferTo(file);

        // Check if the library exists
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid library ID"));

        // Create and initialize the MediaFile entity
        MediaFile mediaFile = new MediaFile();
        mediaFile.setName(originalFileName);
        mediaFile.setUrl(filePath);
        mediaFile.setType(detectMediaType(originalFileName));
        mediaFile.setLibrary(library);

        // Save the MediaFile entity to the database
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
        logger.info("Fetching next media file after ID: {}", currentId);

        List<MediaFile> mediaFiles = mediaRepository.findAll();
        logger.info("Available media IDs: {}", mediaFiles.stream().map(MediaFile::getId).collect(Collectors.toList()));

        // Try to find the next media file
        Optional<MediaFile> nextMedia = mediaRepository.findFirstByIdGreaterThanOrderByIdAsc(currentId);

        // If there is no next file, return the first file in the list
        return nextMedia.orElseGet(() -> {
            logger.info("No next media found, returning the first media file.");
            return mediaFiles.get(0);
        });
    }

    public MediaFile playPrevious(Long currentId) {
        logger.info("Fetching previous media file before ID: {}", currentId);

        List<MediaFile> mediaFiles = mediaRepository.findAll();
        logger.info("Available media IDs: {}", mediaFiles.stream().map(MediaFile::getId).collect(Collectors.toList()));

        // Try to find the previous media file
        Optional<MediaFile> previousMedia = mediaRepository.findFirstByIdLessThanOrderByIdDesc(currentId);

        // If there is no previous file, return the last file in the list
        return previousMedia.orElseGet(() -> {
            logger.info("No previous media found, returning the last media file.");
            return mediaFiles.get(mediaFiles.size() - 1);
        });
    }

    private int findCurrentIndex(List<MediaFile> mediaFiles, Long currentId) {
        for (int i = 0; i < mediaFiles.size(); i++) {
            if (mediaFiles.get(i).getId().equals(currentId)) {
                return i;
            }
        }
        return -1;
    }

    public List<MediaFile> findAll() {
        return mediaRepository.findAll();
    }
}

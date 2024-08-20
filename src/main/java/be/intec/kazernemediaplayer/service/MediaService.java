package be.intec.kazernemediaplayer.service;

import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.repository.LibraryRepository;
import be.intec.kazernemediaplayer.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class MediaService {

    @Value("${media.upload-dir}")
    private String directoryPath;

    private final MediaRepository mediaRepository;
    private final LibraryRepository libraryRepository;

    private MediaFile currentlyPlaying;

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
        String filePath = directoryPath + uniqueFileName;

        // Save the file to the specified path
        File file = new File(filePath);
        multipartFile.transferTo(file);

        // Check if the library exists
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid library ID"));

        // Create and save the MediaFile entity
        MediaFile mediaFile = new MediaFile();
        mediaFile.setName(originalFileName);
        mediaFile.setUrl(filePath);
        mediaFile.setLibrary(library);

        return mediaRepository.save(mediaFile);
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
        List<MediaFile> mediaFiles = mediaRepository.findAll(); // Assuming this is sorted
        int currentIndex = findCurrentIndex(mediaFiles, currentId);
        if (currentIndex >= 0 && currentIndex < mediaFiles.size() - 1) {
            currentlyPlaying = mediaFiles.get(currentIndex + 1);
        } else {
            throw new MediaNotFoundException("Next media file not found");
        }
        return currentlyPlaying;
    }

    public MediaFile playPrevious(Long currentId) {
        List<MediaFile> mediaFiles = mediaRepository.findAll(); // Assuming this is sorted
        int currentIndex = findCurrentIndex(mediaFiles, currentId);
        if (currentIndex > 0) {
            currentlyPlaying = mediaFiles.get(currentIndex - 1);
        } else {
            throw new MediaNotFoundException("Previous media file not found");
        }
        return currentlyPlaying;
    }

    private int findCurrentIndex(List<MediaFile> mediaFiles, Long currentId) {
        for (int i = 0; i < mediaFiles.size(); i++) {
            if (mediaFiles.get(i).getId().equals(currentId)) {
                return i;
            }
        }
        return -1; // Not found
    }

    public List<MediaFile> findAll() {
        return mediaRepository.findAll();
    }
}

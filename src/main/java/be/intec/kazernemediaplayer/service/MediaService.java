package be.intec.kazernemediaplayer.service;


import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    private static final String MEDIA_DIR = "D:/Rendered projects/2024";

    private MediaFile currentlyPlaying;

    public MediaFile uploadMedia(MultipartFile multipartFile, Long libraryId) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        String filePath = "D:/Rendered projects/2024/" + fileName;
        File file = new File(filePath);
        multipartFile.transferTo(file);

        Library library = new Library();
        library.setId(libraryId);

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName(fileName);
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
        Optional<MediaFile> nextMediaFile = mediaRepository.findById(currentId + 1);
        if (nextMediaFile.isPresent()) {
            currentlyPlaying = nextMediaFile.get();
        }
        return currentlyPlaying;
    }

    public MediaFile playPrevious(Long currentId) {
        Optional<MediaFile> previousMediaFile = mediaRepository.findById(currentId - 1);
        if (previousMediaFile.isPresent()) {
            currentlyPlaying = previousMediaFile.get();
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
}
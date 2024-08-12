package be.intec.kazernemediaplayertest.service;

import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.repository.LibraryRepository;
import be.intec.kazernemediaplayer.repository.MediaRepository;
import be.intec.kazernemediaplayer.service.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MediaServiceTest {

    @Mock
    private LibraryRepository libraryRepository;
    @InjectMocks
    private MediaService mediaService;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void uploadMedia() throws IOException {
        // Given
        Long libraryId = 1L;
        String fileName = "Test.wav";
        String filePath = "D:/Rendered projects/2024/";
        Library library = new Library();
        library.setId(libraryId);

        // Mocking the multipart file behaviors
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        doNothing().when(multipartFile).transferTo(any(File.class));

        // Mocking the LibraryRepository to return a valid Library object
        when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));

        // Mocking the MediaRepository to return the saved MediaFile
        MediaFile savedMediaFile = new MediaFile();
        savedMediaFile.setName(fileName);
        savedMediaFile.setUrl(filePath + fileName);
        savedMediaFile.setLibrary(library);
        savedMediaFile.setId(1L);

        when(mediaRepository.save(any(MediaFile.class))).thenReturn(savedMediaFile);

        // When
        MediaFile result = mediaService.uploadMedia(multipartFile, libraryId);

        // Then
        assertNotNull(result);
        assertEquals(fileName, result.getName());
        assertTrue(result.getUrl().startsWith(filePath));
        assertEquals(libraryId, result.getLibrary().getId());

        // Verify interactions
        verify(multipartFile).transferTo(any(File.class));
        verify(libraryRepository).findById(libraryId);  // Verify that findById was called
        verify(mediaRepository).save(any(MediaFile.class));
    }

    @Test
    void playMedia() {
        // Given
        Long mediaId = 1L;
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(mediaId);

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(mediaFile));

        // When
        MediaFile result = mediaService.playMedia(mediaId);

        // Then
        assertNotNull(result);
        assertEquals(mediaId, result.getId());

        // Verify interactions
        verify(mediaRepository).findById(mediaId);
    }

    @Test
    void stopMedia() {
        // Given
        Long mediaId = 1L;
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(mediaId);

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(mediaFile));
        mediaService.playMedia(mediaId); // Simulate playing some media

        // Verify interaction with playMedia
        verify(mediaRepository).findById(mediaId);

        // When
        mediaService.stopMedia();

        // Then
        assertNull(mediaService.getCurrentlyPlaying()); // Verify that currentlyPlaying is null after stopMedia()

        // Verify no unexpected interactions
        verifyNoMoreInteractions(mediaRepository);
    }
}

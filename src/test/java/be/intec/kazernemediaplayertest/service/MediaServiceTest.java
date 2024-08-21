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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.test.util.ReflectionTestUtils;

class MediaServiceTest {

    @Mock
    private LibraryRepository libraryRepository;

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private MediaService mediaService;

    @Mock
    private MultipartFile multipartFile;

    private final String directoryPath = "D:/KazerneMediaPlayer Songs 2024/";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject the directoryPath value into the MediaService instance
        ReflectionTestUtils.setField(mediaService, "directoryPath", directoryPath);

        // Manually inject the mock repositories into the mediaService
        ReflectionTestUtils.setField(mediaService, "libraryRepository", libraryRepository);
        ReflectionTestUtils.setField(mediaService, "mediaRepository", mediaRepository);
    }

    @Test
    void uploadMedia() throws IOException {
        // Given
        Long libraryId = 1L;
        String fileName = "Test.wav";
        Library library = new Library();
        library.setId(libraryId);

        // Mocking the multipart file behaviors
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        doNothing().when(multipartFile).transferTo(any(File.class));

        // Mocking the LibraryRepository to return a valid Library object
        when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));

        // Mocking the MediaRepository to return the saved MediaFile
        MediaFile savedMediaFile = new MediaFile();
        String expectedUrl = directoryPath + System.currentTimeMillis() + "_" + fileName; // Example dynamic file path
        savedMediaFile.setName(fileName);
        savedMediaFile.setUrl(expectedUrl);
        savedMediaFile.setLibrary(library);
        savedMediaFile.setId(1L);

        when(mediaRepository.save(any(MediaFile.class))).thenReturn(savedMediaFile);

        // When
        MediaFile result = mediaService.uploadMedia(multipartFile, libraryId);

        // Then
        assertNotNull(result, "The result should not be null");
        assertTrue(result.getUrl().contains(directoryPath), "The URL should contain the directory path");
        assertTrue(result.getUrl().endsWith(fileName), "The URL should end with the file name");
        assertEquals(libraryId, result.getLibrary().getId(), "The library ID should match");

        // Capturing the MediaFile object passed to mediaRepository.save()
        ArgumentCaptor<MediaFile> mediaFileCaptor = ArgumentCaptor.forClass(MediaFile.class);
        verify(mediaRepository).save(mediaFileCaptor.capture());
        MediaFile capturedMediaFile = mediaFileCaptor.getValue();

        // Additional assertions on the captured MediaFile
        assertTrue(capturedMediaFile.getUrl().contains(directoryPath), "Captured URL should contain directory path");
        assertTrue(capturedMediaFile.getUrl().endsWith(fileName), "Captured URL should end with the file name");
        assertEquals(library, capturedMediaFile.getLibrary(), "Captured library should match the expected library");

        // Verify interactions
        verify(multipartFile).transferTo(any(File.class));
        verify(libraryRepository).findById(libraryId);  // Verify that findById was called
    }


    @Test
    void deleteMedia() throws IOException {
        // Given
        Long mediaId = 1L;
        String filePath = "D:/KazerneMediaPlayer Songs 2024/1724152608124_file.mp3";
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(mediaId);
        mediaFile.setUrl(filePath);

        // Mocking the MediaRepository to return the media file
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(mediaFile));

        // Mocking the file exists scenario
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.delete()).thenReturn(true);

        // When
        mediaService.deleteMedia(mediaId);

        // Then
        verify(mediaRepository).deleteById(mediaId);
    }

    @Test
    void deleteMedia_FileNotFound() throws IOException {
        // Given
        Long mediaId = 1L;
        String filePath = "D:/KazerneMediaPlayer Songs 2024/1724152608124_file.mp3";
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(mediaId);
        mediaFile.setUrl(filePath);

        // Mocking the MediaRepository to return the media file
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(mediaFile));

        // Mocking the file not found scenario
        File file = mock(File.class);
        when(file.exists()).thenReturn(false);

        // When
        mediaService.deleteMedia(mediaId);

        // Then
        verify(mediaRepository).deleteById(mediaId);
        // Log verification can also be added to check if the warning was logged, but it's optional.
    }

    @Test
    void deleteMedia_MediaNotFound() {
        // Given
        Long mediaId = 1L;

        // Mocking the MediaRepository to return an empty optional (media not found)
        when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());

        // Expect an IllegalArgumentException due to the missing media file
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mediaService.deleteMedia(mediaId);
        });

        assertEquals("Media file not found with ID: " + mediaId, exception.getMessage());

        // Verify that deleteById was never called since the media was not found
        verify(mediaRepository, never()).deleteById(mediaId);
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

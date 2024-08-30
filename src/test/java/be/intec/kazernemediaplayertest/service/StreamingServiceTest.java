/*package be.intec.kazernemediaplayertest.service;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.repository.MediaRepository;
import be.intec.kazernemediaplayer.service.StreamingService;
import be.intec.kazernemediaplayer.service.MediaNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StreamingServiceTest {

    private StreamingService streamingService;

    @Mock
    private MediaRepository mediaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        streamingService = new StreamingService(mediaRepository);  // Inject the mock repository via constructor
    }

    @Test
    void testStreamMediaSuccess() {
        // Given
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(1L);
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(mediaFile));

        // When
        String result = streamingService.streamMedia(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("http://localhost:8080/media/1"));
        verify(mediaRepository, times(1)).findById(1L);
    }

    @Test
    void testStreamMediaNotFound() {
        // Given
        when(mediaRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        MediaNotFoundException exception = assertThrows(MediaNotFoundException.class, () -> {
            streamingService.streamMedia(1L);
        });

        assertEquals("Media file with ID 1 not found.", exception.getMessage());
        verify(mediaRepository, times(1)).findById(1L);
    }

    @Test
    void testGenerateSecurityToken() {
        // Given
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(1L);
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(mediaFile));

        // When
        String result = streamingService.streamMedia(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("http://localhost:8080/media/1"));
        assertTrue(result.contains("token=")); // Check that the token is part of the URL
    }
}*/

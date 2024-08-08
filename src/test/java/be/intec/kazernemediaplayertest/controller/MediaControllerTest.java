package be.intec.kazernemediaplayertest.controller;

import be.intec.kazernemediaplayer.KazerneMediaPLayer;
import be.intec.kazernemediaplayer.controller.MediaController;
import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.service.MediaService;
import be.intec.kazernemediaplayer.service.StreamingService;
import be.intec.kazernemediaplayertest.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = KazerneMediaPLayer.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)  // Only if you have a specific test security configuration
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaService mediaService;

    @MockBean
    private StreamingService streamingService; // Add this if needed

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUploadMedia() throws Exception {
        // Given
        Long libraryId = 1L;
        String fileName = "Test.wav";
        String filePath = "D:/Rendered projects/2024/" + fileName;

        // Mock the MediaFile object returned by the service
        MediaFile mediaFile = new MediaFile();
        mediaFile.setName(fileName);
        mediaFile.setUrl(filePath);
        mediaFile.setId(1L);

        when(mediaService.uploadMedia(any(MockMultipartFile.class), anyLong())).thenReturn(mediaFile);

        // Create a mock multipart file
        MockMultipartFile file = new MockMultipartFile(
                "file", // Name of the part in the request
                fileName, // Original filename
                "audio/wav", // Content type
                "dummy file content".getBytes() // File content
        );

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/media/upload")
                        .file(file) // Add the mock file
                        .param("libraryId", libraryId.toString()) // Add other parameters
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(fileName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").value(filePath));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testPlayMedia() throws Exception {
        // Given
        Long currentId = 1L;
        Long nextId = 2L;
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(nextId);

        when(mediaService.playMedia(anyLong())).thenReturn(mediaFile);

        // When & Then
        mockMvc.perform(get("/media/play/{currentId}", currentId)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(nextId));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testStopMedia() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/media/stop")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testPlayNext() throws Exception {
        // Given
        Long currentId = 1L;
        Long nextId = 2L;
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(nextId);

        when(mediaService.playNext(anyLong())).thenReturn(mediaFile);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/media/next/{currentId}", currentId)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(nextId));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testPlayPrevious() throws Exception {
        // Given
        Long currentId = 2L;
        Long previousId = 1L;
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(previousId);

        when(mediaService.playPrevious(anyLong())).thenReturn(mediaFile);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/media/previous/{currentId}", currentId)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(previousId));
    }
}


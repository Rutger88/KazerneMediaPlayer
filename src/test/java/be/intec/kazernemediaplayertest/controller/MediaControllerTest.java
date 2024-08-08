package be.intec.kazernemediaplayertest.controller;

import be.intec.kazernemediaplayer.controller.MediaController;
import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.service.MediaService;
import be.intec.kazernemediaplayertest.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

@Import(TestSecurityConfig.class)
@SpringBootTest(classes = MediaController.class) // Specify your main application class
@AutoConfigureMockMvc
public class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

        // Mock a logged-in user

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testUploadMedia() throws Exception {
        // Given
        Long libraryId = 1L;
        String fileName = "Test.wav";
        String filePath = "D:/Rendered projects/2024/" + fileName;
        MediaFile mediaFile = new MediaFile();
        mediaFile.setName(fileName);
        mediaFile.setUrl(filePath);
        mediaFile.setId(1L);

        when(mediaService.uploadMedia(any(), anyLong())).thenReturn(mediaFile);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/media/upload")
                        .param("libraryId", libraryId.toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content("dummy file content") // Simulate file content if needed
                )
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
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
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
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(nextId));
    }

    @Test
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

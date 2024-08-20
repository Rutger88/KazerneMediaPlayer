package be.intec.kazernemediaplayertest.controller;

import be.intec.kazernemediaplayer.KazerneMediaPLayer;
import be.intec.kazernemediaplayer.config.SecurityConfig;
import be.intec.kazernemediaplayer.controller.MediaController;
import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.service.MediaService;
import be.intec.kazernemediaplayer.service.StreamingService;
//import be.intec.kazernemediaplayertest.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {KazerneMediaPLayer.class, SecurityConfig.class})
@AutoConfigureMockMvc
public class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaService mediaService;

    @MockBean
    private StreamingService streamingService;

    private ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private SecurityFilterChain securityFilterChain;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUploadMedia() throws Exception {
        Long libraryId = 1L;
        String fileName = "Test.wav";
        String filePath = "D:\\KazerneMediaPlayer Songs 2024" + fileName;

        MediaFile mediaFile = new MediaFile();
        mediaFile.setName(fileName);
        mediaFile.setUrl(filePath);
        mediaFile.setId(1L);

        when(mediaService.uploadMedia(any(MockMultipartFile.class), anyLong())).thenReturn(mediaFile);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                fileName,
                "audio/wav",
                "dummy file content".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/media/upload")
                        .file(file)
                        .param("libraryId", libraryId.toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(fileName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").value(filePath));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testPlayMedia() throws Exception {
        Long currentId = 1L;
        Long nextId = 2L;
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(nextId);

        when(mediaService.playMedia(anyLong())).thenReturn(mediaFile);

        mockMvc.perform(get("/media/play/{currentId}", currentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(nextId));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testStopMedia() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/media/stop")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testPlayNext() throws Exception {
        Long currentId = 1L;
        Long nextId = 2L;
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(nextId);

        when(mediaService.playNext(anyLong())).thenReturn(mediaFile);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/media/next/{currentId}", currentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(nextId));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testPlayPrevious() throws Exception {
        Long currentId = 2L;
        Long previousId = 1L;
        MediaFile mediaFile = new MediaFile();
        mediaFile.setId(previousId);

        when(mediaService.playPrevious(anyLong())).thenReturn(mediaFile);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/media/previous/{currentId}", currentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(previousId));
    }
}

package be.intec.kazernemediaplayertest.controller;

import be.intec.kazernemediaplayer.KazerneMediaPLayer;
import be.intec.kazernemediaplayer.controller.StreamingController;
import be.intec.kazernemediaplayer.service.MediaNotFoundException;
import be.intec.kazernemediaplayer.service.StreamingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = KazerneMediaPLayer.class)
@AutoConfigureMockMvc
public class StreamingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StreamingService streamingService;

    @Test
    void testStreamMedia() throws Exception {
        // Given
        Long mediaId = 1L;
        String streamingUrl = "http://streaming.server/media/1?token=abcdef123456";
        when(streamingService.streamMedia(mediaId)).thenReturn(streamingUrl);

        // When & Then
        mockMvc.perform(get("/stream/{mediaId}", mediaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(streamingUrl));
    }

    @Test
    void testStreamMediaNotFound() throws Exception {
        // Given
        Long mediaId = 1L;

        when(streamingService.streamMedia(mediaId)).thenThrow(new MediaNotFoundException("Media not found"));

        // When & Then
        mockMvc.perform(get("/stream/{mediaId}", mediaId)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Media not found"));
    }
}

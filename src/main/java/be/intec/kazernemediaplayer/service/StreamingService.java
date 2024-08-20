package be.intec.kazernemediaplayer.service;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;

@Service
public class StreamingService {

    private final MediaRepository mediaRepository;
    private static final Logger logger = LoggerFactory.getLogger(StreamingService.class);
    private static final String BASE_STREAMING_URL = "http://localhost:8080/media/";

    @Autowired
    public StreamingService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    // Existing method to generate a secure URL for media streaming
    public String streamMedia(Long mediaId) {
        Optional<MediaFile> mediaFileOptional = mediaRepository.findById(mediaId);

        if (mediaFileOptional.isPresent()) {
            MediaFile mediaFile = mediaFileOptional.get();
            logger.info("Streaming media file with ID: " + mediaId);
            return generateStreamingUrl(mediaFile);
        } else {
            logger.error("Media file with ID " + mediaId + " not found.");
            throw new MediaNotFoundException("Media file with ID " + mediaId + " not found.");
        }
    }

    // New method to get the actual media file as a Resource
    public Resource getMediaFileResource(Long mediaId) throws MediaNotFoundException {
        Optional<MediaFile> mediaFileOptional = mediaRepository.findById(mediaId);

        if (mediaFileOptional.isPresent()) {
            MediaFile mediaFile = mediaFileOptional.get();
            File mediaFileLocation = new File(mediaFile.getUrl());

            if (mediaFileLocation.exists()) {
                return new FileSystemResource(mediaFileLocation);
            } else {
                throw new MediaNotFoundException("Media file with ID " + mediaId + " does not exist on disk.");
            }
        } else {
            throw new MediaNotFoundException("Media file with ID " + mediaId + " not found in database.");
        }
    }

    private String generateStreamingUrl(MediaFile mediaFile) {
        String securityToken = generateSecurityToken(mediaFile.getId());
        return BASE_STREAMING_URL + mediaFile.getId() + "?token=" + securityToken;
    }

    private String generateSecurityToken(Long mediaId) {
        String secretKey = "your-secret-key"; // Replace with your actual secret key
        long timestamp = Instant.now().getEpochSecond();
        String data = mediaId + ":" + timestamp;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((data + secretKey).getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString() + ":" + timestamp;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating security token", e);
        }
    }
}

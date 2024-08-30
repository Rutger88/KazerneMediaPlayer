package be.intec.kazernemediaplayer.service;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;

@Service
public class StreamingService {

    private static final Logger logger = LoggerFactory.getLogger(StreamingService.class);

    // URL for streaming (could be moved to application.properties)
    private static final String BASE_STREAMING_URL = "http://localhost:8080/media/";

    // Secret key from application properties for token generation
    @Value("${streaming.secret.key}")
    private String secretKey;

    @Value("${streaming.token.expiry.seconds}")
    private long tokenExpirySeconds;  // E.g., 3600 for 1 hour

    private final MediaRepository mediaRepository;

    @Autowired
    public StreamingService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    // Generate secure URL for media streaming
    public String streamMedia(Long mediaId) {
        Optional<MediaFile> mediaFileOptional = mediaRepository.findById(mediaId);

        if (mediaFileOptional.isPresent()) {
            MediaFile mediaFile = mediaFileOptional.get();
            logger.info("Streaming media file with ID: {}", mediaId);
            return generateStreamingUrl(mediaFile);
        } else {
            logger.error("Media file with ID {} not found.", mediaId);
            throw new MediaNotFoundException("Media file with ID " + mediaId + " not found.");
        }
    }

    // Get the actual media file as a Resource
    public Resource getMediaFileResource(Long mediaId) throws MediaNotFoundException {
        Optional<MediaFile> mediaFileOptional = mediaRepository.findById(mediaId);

        if (mediaFileOptional.isPresent()) {
            MediaFile mediaFile = mediaFileOptional.get();
            File mediaFileLocation = new File(mediaFile.getUrl());

            if (mediaFileLocation.exists() && mediaFileLocation.isFile()) {
                logger.info("Found media file at path: {}", mediaFile.getUrl());
                return new FileSystemResource(mediaFileLocation);
            } else {
                logger.error("Media file does not exist on disk: {}", mediaFile.getUrl());
                throw new MediaNotFoundException("Media file with ID " + mediaId + " does not exist on disk.");
            }
        } else {
            logger.error("Media file with ID {} not found in the database.", mediaId);
            throw new MediaNotFoundException("Media file with ID " + mediaId + " not found in database.");
        }
    }

    // Generate a secure URL for streaming
    private String generateStreamingUrl(MediaFile mediaFile) {
        String securityToken = generateSecurityToken(mediaFile.getId());
        return BASE_STREAMING_URL + mediaFile.getId() + "?token=" + securityToken;
    }

    // Generate security token with SHA-256 hashing
    private String generateSecurityToken(Long mediaId) {
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
            logger.error("Error generating security token for media ID: {}", mediaId, e);
            throw new RuntimeException("Error generating security token", e);
        }
    }

    // Verify if the token is valid (e.g., within the expiry time)
    public boolean isTokenValid(String token, Long mediaId) {
        String[] parts = token.split(":");
        if (parts.length != 2) {
            logger.warn("Invalid token format for media ID: {}", mediaId);
            return false;
        }

        try {
            String receivedHash = parts[0];
            long timestamp = Long.parseLong(parts[1]);
            long currentTime = Instant.now().getEpochSecond();

            // Check if token is expired
            if (currentTime - timestamp > tokenExpirySeconds) {
                logger.warn("Token expired for media ID: {}", mediaId);
                return false;
            }

            // Recompute the hash to validate it
            String validToken = generateSecurityToken(mediaId);
            String validHash = validToken.split(":")[0];

            if (receivedHash.equals(validHash)) {
                logger.info("Token validated successfully for media ID: {}", mediaId);
                return true;
            } else {
                logger.warn("Invalid token hash for media ID: {}", mediaId);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error validating token for media ID: {}", mediaId, e);
            return false;
        }
    }
}

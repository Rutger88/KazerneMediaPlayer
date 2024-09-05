package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.repository.MediaRepository;
import be.intec.kazernemediaplayer.service.MediaNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/stream")
public class StreamingController {

    @Autowired
    private MediaRepository mediaRepository;

    @GetMapping("/{mediaId}")
    public ResponseEntity<Resource> streamMedia(
            @PathVariable Long mediaId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        try {
            // Fetch the media file based on media ID from the database
            Optional<MediaFile> mediaFileOptional = mediaRepository.findById(mediaId);
            if (mediaFileOptional.isEmpty()) {
                throw new MediaNotFoundException("Media file not found for ID: " + mediaId);
            }

            MediaFile mediaFile = mediaFileOptional.get();
            Path mediaPath = Paths.get(mediaFile.getFilePath());

            // Check if the file exists and is readable
            if (!Files.exists(mediaPath) || !Files.isReadable(mediaPath)) {
                throw new MediaNotFoundException("Media file is not accessible or does not exist: " + mediaFile.getFilePath());
            }

            // Load the resource
            Resource mediaResource = new InputStreamResource(Files.newInputStream(mediaPath));
            String mediaType = Files.probeContentType(mediaPath);
            mediaType = (mediaType == null) ? "application/octet-stream" : mediaType;

            long fileSize = Files.size(mediaPath);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, mediaType);

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                // Parse the Range header and extract start and end positions
                HttpRange range = HttpRange.parseRanges(rangeHeader).get(0);
                long rangeStart = range.getRangeStart(fileSize);
                long rangeEnd = range.getRangeEnd(fileSize);
                long contentLength = rangeEnd - rangeStart + 1;

                // Open InputStream for the requested byte range
                try (InputStream inputStream = Files.newInputStream(mediaPath)) {
                    inputStream.skip(rangeStart); // Skip to the start of the range

                    headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize);
                    headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");

                    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                            .headers(headers)
                            .contentLength(contentLength)
                            .body(new InputStreamResource(inputStream));
                }
            } else {
                // Full content (first request, no range)
                headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");
                headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(fileSize)
                        .body(mediaResource);
            }
        } catch (MediaNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading media file", e);
        }
    }

    @ExceptionHandler(MediaNotFoundException.class)
    public ResponseEntity<String> handleMediaNotFoundException(MediaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
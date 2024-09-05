package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.config.CustomUserDetailService;
import be.intec.kazernemediaplayer.config.CustomUserDetails;
import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.model.MediaFile;
import be.intec.kazernemediaplayer.repository.LibraryRepository;
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
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/stream")
public class StreamingController {

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private LibraryRepository libraryRepository;


    @GetMapping("/{mediaId}")
    public ResponseEntity<Resource> streamMedia(
            @PathVariable Long mediaId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        try {
            // Step 1: Get the currently authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getId(); // Get the userId from CustomUserDetails

            // Step 2: Fetch the user's library using their user ID
            Library userLibrary = libraryRepository.findFirstByUserId(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have a library"));

            // Step 3: Fetch the media file based on mediaId and libraryId
            Optional<MediaFile> mediaFileOptional = mediaRepository.findByIdAndLibraryId(mediaId, userLibrary.getId());
            if (mediaFileOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this media file");
            }

            MediaFile mediaFile = mediaFileOptional.get();
            Path mediaPath = Paths.get(mediaFile.getFilePath());

            // Step 4: File validation and streaming logic
            if (!Files.exists(mediaPath) || !Files.isReadable(mediaPath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Media file is not accessible or does not exist");
            }

            String mediaType = Files.probeContentType(mediaPath);
            mediaType = (mediaType == null) ? "application/octet-stream" : mediaType;

            long fileSize = Files.size(mediaPath);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, mediaType);

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                HttpRange range = HttpRange.parseRanges(rangeHeader).get(0);
                long rangeStart = range.getRangeStart(fileSize);
                long rangeEnd = range.getRangeEnd(fileSize);
                long contentLength = rangeEnd - rangeStart + 1;

                RandomAccessFile randomAccessFile = new RandomAccessFile(mediaPath.toFile(), "r");
                randomAccessFile.seek(rangeStart);

                InputStreamResource inputStreamResource = new InputStreamResource(new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return randomAccessFile.read();
                    }

                    @Override
                    public int read(byte[] b, int off, int len) throws IOException {
                        return randomAccessFile.read(b, off, len);
                    }

                    @Override
                    public void close() throws IOException {
                        randomAccessFile.close();
                    }
                });

                headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize);
                headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .headers(headers)
                        .contentLength(contentLength)
                        .body(inputStreamResource);
            } else {
                headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");
                headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));

                Resource mediaResource = new InputStreamResource(Files.newInputStream(mediaPath));
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(fileSize)
                        .body(mediaResource);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading media file", e);
        }
    }

}

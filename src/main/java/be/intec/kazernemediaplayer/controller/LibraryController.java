package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/libraries")
public class LibraryController {

    private final LibraryService libraryService;

    @Autowired
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // Get all libraries for a specific user
    @GetMapping("/getlibraries/{userId}")
    public ResponseEntity<List<Library>> getSharedLibraries(@PathVariable Long userId) {
        List<Library> libraries = libraryService.getSharedLibraries(userId);
        return ResponseEntity.ok(libraries);
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<Library> addLibrary(@PathVariable Long userId, @RequestBody Library library) {
        // Adding the library for the user
        Library savedLibrary = libraryService.addLibrary(library, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLibrary);
    }

    // Get a specific library by ID
    @GetMapping("/{libraryId}")
    public ResponseEntity<Library> getLibraryById(@RequestHeader("userId") Long userId, @PathVariable Long libraryId) {
        // Fetch the library, ensuring it belongs to the user
        Library library = libraryService.getLibraryById(userId, libraryId);
        return ResponseEntity.ok(library);
    }

    // Update an existing library
    @PutMapping("/{libraryId}")
    public ResponseEntity<Library> updateLibrary(@PathVariable Long libraryId, @RequestBody Library library) {
        library.setId(libraryId);  // Ensure the library ID is set correctly
        Library updatedLibrary = libraryService.updateLibrary(library);
        return ResponseEntity.ok(updatedLibrary);
    }
    @PostMapping("/share/{userId}")
    public ResponseEntity<String> shareLibraryWithUser(
            @RequestParam Long libraryId,
            @RequestParam Long targetUserId) {

        libraryService.shareLibraryWithUser(libraryId, targetUserId);
        return ResponseEntity.ok("Library shared successfully.");
    }
    @GetMapping("/others/{currentUserId}")
    public ResponseEntity<List<Library>> getOtherUsersLibraries(@PathVariable Long currentUserId) {
        List<Library> otherLibraries = libraryService.getOtherUsersLibraries(currentUserId);
        return ResponseEntity.ok(otherLibraries);
    }

    // Delete a library by ID
    @DeleteMapping("/delete/{libraryId}")
    public ResponseEntity<Void> deleteLibrary(@PathVariable Long libraryId) {
        libraryService.deleteLibrary(libraryId);
        return ResponseEntity.noContent().build();  // Return 204 No Content on successful deletion
    }
}

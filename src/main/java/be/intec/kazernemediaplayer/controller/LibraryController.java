package be.intec.kazernemediaplayer.controller;

import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Library>> getSharedLibraries(@PathVariable Long userId) {
        List<Library> libraries = libraryService.getSharedLibraries(userId);
        return ResponseEntity.ok(libraries);
    }

    // Add a new library for a specific user
    @PostMapping("/user/{userId}")
    public ResponseEntity<Library> addLibrary(@PathVariable Long userId, @RequestBody Library library) {
        Library savedLibrary = libraryService.addLibrary(library, userId);
        return ResponseEntity.ok(savedLibrary);
    }

    // Get a specific library by ID
    @GetMapping("/{libraryId}")
    public ResponseEntity<Library> getLibraryById(@PathVariable Long libraryId) {
        Library library = libraryService.getLibraryById(libraryId);
        return ResponseEntity.ok(library);
    }

    // Update an existing library
    @PutMapping("/{libraryId}")
    public ResponseEntity<Library> updateLibrary(@PathVariable Long libraryId, @RequestBody Library library) {
        library.setId(libraryId);  // Ensure the library ID is set correctly
        Library updatedLibrary = libraryService.updateLibrary(library);
        return ResponseEntity.ok(updatedLibrary);
    }

    // Delete a library by ID
    @DeleteMapping("/{libraryId}")
    public ResponseEntity<Void> deleteLibrary(@PathVariable Long libraryId) {
        libraryService.deleteLibrary(libraryId);
        return ResponseEntity.noContent().build();  // Return 204 No Content on successful deletion
    }
}

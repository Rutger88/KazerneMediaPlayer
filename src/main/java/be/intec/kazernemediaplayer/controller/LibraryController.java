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

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/user/{ownerId}")
    public ResponseEntity<List<Library>> getSharedLibraries(@PathVariable Long ownerId) {
        return ResponseEntity.ok(libraryService.getSharedLibraries(ownerId));
    }

    @PostMapping
    public ResponseEntity<Library> addLibrary(@RequestBody Library library) {
        return ResponseEntity.ok(libraryService.addLibrary(library));
    }
}

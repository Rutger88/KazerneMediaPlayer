package be.intec.kazernemediaplayer.service;

import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.model.User;
import be.intec.kazernemediaplayer.repository.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {
    @Autowired
    private LibraryRepository libraryRepository;

    public List<Library> getSharedLibraries(Long ownerId) {
        return libraryRepository.findAllByOwnerId(ownerId);
    }

    public Library addLibrary(Library library) {
        return libraryRepository.save(library);
    }

    public Library deleteLibrary(Long libraryId) {
        Optional<Library> libraryOptional = libraryRepository.findById(libraryId);
        if (libraryOptional.isPresent()) {
            Library library = libraryOptional.get();
            libraryRepository.deleteById(libraryId);
            return library; // Return the deleted Library
        } else {
            throw new RuntimeException("Library with ID " + libraryId + " not found");
        }
    }
}
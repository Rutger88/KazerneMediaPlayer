package be.intec.kazernemediaplayer.service;

import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.model.User;
import be.intec.kazernemediaplayer.repository.LibraryRepository;
import be.intec.kazernemediaplayer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final UserRepository userRepository;

    @Autowired
    public LibraryService(LibraryRepository libraryRepository, UserRepository userRepository) {
        this.libraryRepository = libraryRepository;
        this.userRepository = userRepository;
    }

    public List<Library> getSharedLibraries(Long userId) {
        // Fetch user and handle non-existence
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        // Fetch and return libraries for the user
        return libraryRepository.findAllByUserId(userId);
    }

    public Library addLibrary(Library library, Long userId) {
        // Fetch user and handle non-existence
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        // Set user to library and save
        library.setUser(user);
        return libraryRepository.save(library);
    }

    public Library updateLibrary(Library library) {
        // Ensure library exists before updating
        if (!libraryRepository.existsById(library.getId())) {
            throw new RuntimeException("Library with ID " + library.getId() + " not found");
        }
        return libraryRepository.save(library);
    }

    public Library deleteLibrary(Long libraryId) {
        // Fetch library and handle non-existence
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new RuntimeException("Library with ID " + libraryId + " not found"));

        // Delete and return the deleted library
        libraryRepository.deleteById(libraryId);
        return library;
    }

    public List<Library> getAllLibraries() {
        return libraryRepository.findAll();
    }

    public Library getLibraryById(Long libraryId) {
        return libraryRepository.findById(libraryId)
                .orElseThrow(() -> new RuntimeException("Library with ID " + libraryId + " not found"));
    }
}

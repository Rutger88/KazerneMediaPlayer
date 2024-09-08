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
        // Check if the user is restricted to library 1
        if (userId == 1L) {
            // Return only library 1 for user ID 1
            return libraryRepository.findById(1L).map(List::of).orElseThrow(() -> new RuntimeException("Library 1 not found"));
        }
        // Fetch and return all libraries for other users
        return libraryRepository.findAllByUserId(userId);
    }

    public Library addLibrary(Library library, Long userId) {
        // Null check for the library object
        if (library == null) {
            throw new IllegalArgumentException("Library cannot be null");
        }

        // Fetch user and handle non-existence
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        // Set the user to the library and save it
        library.setUser(user);
        return libraryRepository.save(library);
    }

    public List<Library> getOtherUsersLibraries(Long currentUserId) {
        // Fetch all libraries except the current user's own libraries
        return libraryRepository.findByUserIdNot(currentUserId);
    }

    public void shareLibraryWithUser(Long libraryId, Long targetUserId) {
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new RuntimeException("Library not found"));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Here you could add the logic to track sharing,
        // for example, adding an entry to a `SharedLibrary` table.
        // In this case, we'll just print a message for simplicity.

        System.out.println("Library " + libraryId + " shared with user " + targetUserId);
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

    public Library getLibraryById(Long userId, Long libraryId) {
        // Fetch library by ID and userId to ensure user owns the library
        return libraryRepository.findById(libraryId)
                .filter(library -> library.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Library not found or does not belong to the user."));
    }
}

/*package be.intec.kazernemediaplayer.service;

import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.model.User;
import be.intec.kazernemediaplayer.repository.LibraryRepository;
import be.intec.kazernemediaplayer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LibraryRepository libraryRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create a sample user
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user = userRepository.save(user);

        // Create a sample library for the user
        Library library = new Library();
        library.setName("John's Music Library");
        library.setUser(user);
        libraryRepository.save(library);
    }
}*/
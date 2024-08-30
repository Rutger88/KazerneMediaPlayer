/*package be.intec.kazernemediaplayertest.service;

import be.intec.kazernemediaplayer.KazerneMediaPLayer;
import be.intec.kazernemediaplayer.model.Library;
import be.intec.kazernemediaplayer.repository.LibraryRepository;
import be.intec.kazernemediaplayer.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@SpringBootTest(classes = KazerneMediaPLayer.class)
public class LibraryServiceTest {

    @Mock
    private LibraryRepository libraryRepository;

    @InjectMocks
    private LibraryService libraryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetSharedLibraries() {
        Long ownerId = 1L;
        Library library1 = new Library();
        Library library2 = new Library();
        when(libraryRepository.findAllById(ownerId)).thenReturn(Arrays.asList(library1, library2));

        var libraries = libraryService.getSharedLibraries(ownerId);

        assertNotNull(libraries);
        assertEquals(2, libraries.size());
        verify(libraryRepository, times(1)).findAllById(ownerId);
    }

   @Test
    public void testAddLibrary() {
        Library library = new Library();
        when(libraryRepository.save(library)).thenReturn(library);

        Library savedLibrary = libraryService.addLibrary(library, Long userId);

        assertNotNull(savedLibrary);
        verify(libraryRepository, times(1)).save(library);
    }
    @Test
    public void testDeleteLibrary() {
        Long libraryId = 1L;
        Library library = new Library();
        when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
        doNothing().when(libraryRepository).deleteById(libraryId);

        Library deletedLibrary = libraryService.deleteLibrary(libraryId);

        assertNotNull(deletedLibrary);
        assertEquals(library, deletedLibrary);
        verify(libraryRepository, times(1)).findById(libraryId);
        verify(libraryRepository, times(1)).deleteById(libraryId);
    }

    @Test
    public void testDeleteLibraryNotFound() {
        Long libraryId = 1L;
        when(libraryRepository.findById(libraryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            libraryService.deleteLibrary(libraryId);
        });

        assertEquals("Library with ID " + libraryId + " not found", exception.getMessage());
        verify(libraryRepository, times(1)).findById(libraryId);
        verify(libraryRepository, never()).deleteById(libraryId);
    }
}*/

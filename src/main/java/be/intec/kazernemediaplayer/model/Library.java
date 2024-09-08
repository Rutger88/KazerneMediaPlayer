package be.intec.kazernemediaplayer.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Library {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<MediaFile> mediaFiles = new ArrayList<>();

    // Constructors
    public Library() {}

    public Library(String name, User user) {
        this.name = name;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<MediaFile> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(List<MediaFile> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    // Add a media file to the library
    public void addMediaFile(MediaFile mediaFile) {
        mediaFiles.add(mediaFile);
        mediaFile.setLibrary(this);  // Set the correct library reference
    }

    // Remove a media file from the library
    public void removeMediaFile(MediaFile mediaFile) {
        mediaFiles.remove(mediaFile);
        mediaFile.setLibrary(null);  // Remove the library reference
    }
}


package be.intec.kazernemediaplayer.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Library {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ownerId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "library")
    private Set<MediaFile> mediaFiles;

    public Library() {
    }

    public Library(String name) {
        this.name = name;
    }

    public Long getId() {
        return ownerId;
    }

    public void setId(Long id) {
        this.ownerId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<MediaFile> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(Set<MediaFile> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }
}

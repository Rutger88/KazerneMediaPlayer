package be.intec.kazernemediaplayer.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Data
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String name;

    @Column(length = 100)
    private String type;

    @Column(length = 500)
    private String url;

    @Column(length = 1000)
    private String filePath; // Ensure this is set and used correctly

    @Column
    private Integer duration; // in seconds

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", nullable = false)
    @JsonBackReference
    private Library library;

    @Column
    private Boolean isPlaying = false; // Default value directly

    // Constructors, getters, and setters

    public MediaFile() {
    }

    public MediaFile(String name, String type, String url, int duration, String description, Library library, String filePath) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.duration = duration;
        this.description = description;
        this.library = library;
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "MediaFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", filePath='" + filePath + '\'' +
                ", duration=" + duration +
                ", description='" + description + '\'' +
                ", library=" + library +
                ", isPlaying=" + isPlaying +
                '}';
    }
}
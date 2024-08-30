package be.intec.kazernemediaplayer.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Objects;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
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
    @JoinColumn(name = "library_id")
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

    // Getters and Setters for all fields, including filePath

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Boolean getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(Boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaFile mediaFile = (MediaFile) o;
        return Objects.equals(id, mediaFile.id) &&
                Objects.equals(name, mediaFile.name) &&
                Objects.equals(type, mediaFile.type) &&
                Objects.equals(url, mediaFile.url) &&
                Objects.equals(filePath, mediaFile.filePath) &&
                Objects.equals(duration, mediaFile.duration) &&
                Objects.equals(description, mediaFile.description) &&
                Objects.equals(library, mediaFile.library) &&
                Objects.equals(isPlaying, mediaFile.isPlaying);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, url, filePath, duration, description, library, isPlaying);
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
                ", library=" + (library != null ? library.getId() : "null") +
                ", isPlaying=" + isPlaying +
                '}';
    }

    public String getPath() {
        if (this.filePath != null) {
            return this.filePath;
        } else if (this.library != null) {
            return "/libraries/" + library.getId() + "/media/" + this.name;
        } else {
            throw new IllegalStateException("MediaFile path cannot be determined");
        }
    }
}

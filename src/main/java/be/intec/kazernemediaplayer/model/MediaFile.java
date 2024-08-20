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

    @Column
    private int duration; // in seconds

    @Column(length = 1000)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id")
    @JsonBackReference
    private Library library;

    public MediaFile() {
    }

    public MediaFile(String name, String type, String url,int duration, String description, Library library) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.duration = duration;
        this.description = description;
        this.library = library;
    }

    public MediaFile(Long id, String name, String type, String url,int duration, String description, Library library) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.duration = duration;
        this.description = description;
        this.library = library;
    }

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

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
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
                Objects.equals(description, mediaFile.description) &&
                Objects.equals(library, mediaFile.library);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, url,duration, description, library);
    }
    @Override
    public String toString() {
        return "MediaFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", duration=" + duration +
                ", description='" + description + '\'' +
                ", library=" + (library != null ? library.getId() : "null") +
                '}';
    }
}


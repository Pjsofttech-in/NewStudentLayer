package Layer.NewStudentManagement.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentWebsiteHeaders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String url;

    @Column(name = "sort_order", columnDefinition = "int default 0")
    private Integer sortOrder = 0;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

    @Column(columnDefinition = "varchar(50) default '_self'")
    private String target = "_self";

    // --- The Nested Relationship ---

    // This maps the 'parent_id' column back to this same table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private StudentWebsiteHeaders parent;

    // This creates a list of children for easy access in your Java code
    // CascadeType.ALL and orphanRemoval = true ensure that deleting a parent deletes its children
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<StudentWebsiteHeaders> children = new ArrayList<>();

    // Helper method to keep both sides of the relationship in sync
    public void addChild(StudentWebsiteHeaders child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(StudentWebsiteHeaders child) {
        children.remove(child);
        child.setParent(null);
    }
}
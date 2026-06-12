package yosadchuk.needle.flow.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Design {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "designer_id")
    private Designer designer;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "status")
    private DesignStatus status;
}

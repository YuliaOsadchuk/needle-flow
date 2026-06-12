package yosadchuk.needle.flow.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DesignThread {

    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "designer_id")
    private Design design;

    @ManyToOne
    @JoinColumn(name = "thread_id")
    private Thread thread;

    private double requiredMeters;
}

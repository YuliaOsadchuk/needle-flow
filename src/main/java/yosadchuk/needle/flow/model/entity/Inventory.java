package yosadchuk.needle.flow.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Inventory {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "thread_id")
    private Thread thread;

    private Integer skeinQuantity;

    private Double bobbinQuantity;
}

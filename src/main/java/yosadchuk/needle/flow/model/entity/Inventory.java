package yosadchuk.needle.flow.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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

    private BigDecimal bobbinQuantity;
}

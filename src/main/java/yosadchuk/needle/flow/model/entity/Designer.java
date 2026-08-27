package yosadchuk.needle.flow.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Designer {

    @Id
    private Integer id;

    private String name;
}

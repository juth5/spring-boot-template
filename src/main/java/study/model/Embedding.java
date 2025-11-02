package study.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Embedding {
    private Long id;
    private String content;
    private double[] embedding;
    private Double distance;
}

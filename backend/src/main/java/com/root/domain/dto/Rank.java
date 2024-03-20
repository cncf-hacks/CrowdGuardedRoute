package com.root.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Rank  {

    private Double sum;

    @Setter
    private int rank;

    private List<Score> score;

    @Builder
    private Rank(double sum, List<Score> score) {
        this.sum = sum;
        this.score = score;
    }
}


package com.example.covoiturage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "points_arret",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"trajet_id", "ordre"}
                ),
                @UniqueConstraint(
                        columnNames = {"trajet_id", "ville"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class PointArret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String ville;

    @NotNull
    @Min(0)
    private Integer ordre;

    @NotNull
    private LocalDateTime heurePassage;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal prixDepuisDepart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajet_id", nullable = false)
    private Trajet trajet;
}

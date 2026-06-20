package sn.uchk.uchk_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatistiquesInsertionDTO {
    private long emploiSalarie;
    private long autoEmploi;
    private long poursuiteEtudes;
    private long sansEmploi;
    private long total;
}
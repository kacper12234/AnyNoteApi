package com.betacom.anynoteapi.item.dto;

import java.time.Instant;

public record ItemHistoryResponse(Integer revision, String revisionType, Instant timestamp, String changedBy,
                                  String title, String content) {
}

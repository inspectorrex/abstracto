package dev.sheldan.abstracto.core.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaginatorFooterModel {
    private Integer page;
    private Integer pageCount;
}

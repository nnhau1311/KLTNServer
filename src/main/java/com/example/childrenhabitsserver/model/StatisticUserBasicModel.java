package com.example.childrenhabitsserver.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticUserBasicModel {
    private Integer totalUser;
    private Integer numberUserDisable;
    private Float percentUserDisable;
    private Integer numberUserInActive;
    private Float percentUserInActive;
    private Integer numberUserActive;
    private Float percentUserActive;
}

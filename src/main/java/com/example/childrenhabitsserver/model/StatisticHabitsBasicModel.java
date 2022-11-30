package com.example.childrenhabitsserver.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticHabitsBasicModel {
    private Integer totalHabits;
    private Integer numberHabitsDisable;
    private Float percentHabitsDisable;
    private Integer numberHabitsActive;
    private Float percentHabitsActive;
}

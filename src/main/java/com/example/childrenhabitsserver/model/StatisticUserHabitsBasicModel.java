package com.example.childrenhabitsserver.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticUserHabitsBasicModel {
    private Integer numberCreateNewHabits;
    private Integer numberStartExecuteNewHabits;
    private Integer numberDoneHabits;
    private Integer numberInProcessHabits;
}

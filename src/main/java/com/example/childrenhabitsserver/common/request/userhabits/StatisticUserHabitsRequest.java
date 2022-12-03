package com.example.childrenhabitsserver.common.request.userhabits;

import com.example.childrenhabitsserver.model.UserHabitsContent;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticUserHabitsRequest {
    private String timeStatistic;
    private Integer monthStatistic;
    private Date startDateFilter;
    private Date endDateFilter;
}


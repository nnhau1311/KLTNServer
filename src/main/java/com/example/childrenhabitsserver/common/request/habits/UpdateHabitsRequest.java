package com.example.childrenhabitsserver.common.request.habits;

import com.example.childrenhabitsserver.model.HabitsContent;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateHabitsRequest {

    private String habitsId;
    private String habitsName;
    private String habitsType;
    private String typeOfFinishCourse;
    private List<HabitsContent> habitsContentList;
}


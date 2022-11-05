package com.example.childrenhabitsserver.common.request.habits;

import com.example.childrenhabitsserver.model.HabitsContent;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateHabitsRequest {

    private String habitsName;
    private String habitsType;
    private String typeOfFinishCourse = "percentage";
    private String totalCourse = "100";
    private List<HabitsContent> habitsContentList;
}


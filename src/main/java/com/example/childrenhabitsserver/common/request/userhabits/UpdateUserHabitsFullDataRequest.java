package com.example.childrenhabitsserver.common.request.userhabits;

import com.example.childrenhabitsserver.model.UserHabitsContent;
import lombok.*;

import javax.persistence.ElementCollection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserHabitsFullDataRequest {
    private String id;
    private String userId;
    private String habitsId;
    private String habitsName;
    private String habitsType;
    private String typeOfFinishCourse; // Loại hoàn thành hạn mục này
    private String totalCourse;
    private String executeCourse;
    private Integer status = 0; // Mặc định 0 là chưa hoàn thành
    private Date startDate;
    private Date endDate;
    private Date createDate;
    private Date updateDate;

    private List<UserHabitsContent> habitsContents;
}


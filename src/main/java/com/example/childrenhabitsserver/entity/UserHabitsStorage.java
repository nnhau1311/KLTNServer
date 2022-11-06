package com.example.childrenhabitsserver.entity;

import com.example.childrenhabitsserver.model.HabitsContent;
import com.example.childrenhabitsserver.model.UserHabitsContent;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user_habits")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserHabitsStorage {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "user_habitsId")
    private String id;

    private String userId;
    private String habitsId;
    private String habitsName;
    private String habitsType;
    private String typeOfFinishCourse; // Loại hoàn thành hạn mục này
//    private String totalLevelComplete; // Mức độ hoàn thành dưới dạng chữ (mức độ)
//    private Double totalPercentComplete; // Mức độ hoàn thành phần trăm
    private String totalCourse;
    private String executeCourse;
    private Integer status = 0; // Mặc định 0 là chưa hoàn thành
    private Date startDate;
    private Date endDate;
    private Date createDate;
    private Date updateDate;
    @ElementCollection
    private List<UserHabitsContent> habitsContents;

}


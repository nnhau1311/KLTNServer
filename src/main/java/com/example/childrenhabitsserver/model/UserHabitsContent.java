package com.example.childrenhabitsserver.model;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "user_habits_Content")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserHabitsContent {
    private String contentCode; //Định danh nội dung
    private String typeContent; //Định dạng nội dung
    private String body; //Định dạng nội dung
    private String typeOfFinishCourse; // Loại hoàn thành hạn mục này
    private Integer numberDateExecute;
//    private String levelComplete; // Mức độ hoàn thành dưới dạng chữ (mức độ)
//    private Double percentComplete; // Mức độ hoàn thành phần trăm
    private String totalCourse = "0";
    private String executeCourse = "0";
    private Integer status = 0; // Mặc định 0 là chưa hoàn thành
    private Date startDate;
    private Date endDate;
    private Date updateDate;
}

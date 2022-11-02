package com.example.childrenhabitsserver.model;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Table;

@Table(name = "user_habits_Content")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserHabitsContent {
    private String typeContent; //Định dạng nội dung
    private String body; //Định dạng nội dung
    private String typeOfFinishCourse; // Loại hoàn thành hạn mục này
    private String levelComplete; // Mức độ hoàn thành dưới dạng chữ (mức độ)
    private Double percentComplete; // Mức độ hoàn thành phần trăm
}

package com.example.childrenhabitsserver.model;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Table;

@Table(name = "habits_Content")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class HabitsContent {
    private String typeOfFinishCourse; // Loại định nghĩa hoàn thành theo % hay cột mốc
    private String typeContent; //Định dạng nội dung
    private String body; //Định dạng nội dung

}

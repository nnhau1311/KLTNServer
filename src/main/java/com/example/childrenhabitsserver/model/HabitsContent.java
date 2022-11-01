package com.example.childrenhabitsserver.model;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class HabitsContent {
    private String typeContent; //Định dạng nội dung
    private String body; //Định dạng nội dung

}

package com.example.childrenhabitsserver.entity;

import com.example.childrenhabitsserver.base.database.JPACustomType;
import com.example.childrenhabitsserver.model.HabitsContent;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "habits")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HabitsStorage {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "habitsId")
    private String id;
    private String habitsName;
    private String habitsType;
    @Type(type = JPACustomType.JSONB)
    @Column(columnDefinition = JPACustomType.JSONB_DEF)
    private List<HabitsContent> habitsContentList;
    private String content;

}


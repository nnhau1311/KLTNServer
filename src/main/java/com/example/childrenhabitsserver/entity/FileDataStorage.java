package com.example.childrenhabitsserver.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "file_data_storage")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDataStorage {
    @Id
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "file_data_storage_id")
    private String id;
    private String fileName;
    private String fileNameInServer;
    private String pathFile;
}

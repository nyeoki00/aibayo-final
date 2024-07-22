package com.aico.aibayo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "class_teacher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ClassTeacherEntity {
    @Id
    @Column(name = "class_no")
    private Long classNo;
    @Id
    @Column(name = "class_teacher_id")
    private Long classTeacherId;
    @Id
    @Column(name = "accept_no")
    private Long acceptNo;
}

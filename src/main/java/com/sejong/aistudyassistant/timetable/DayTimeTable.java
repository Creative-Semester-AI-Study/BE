package com.sejong.aistudyassistant.timetable;

import com.sejong.aistudyassistant.subject.Subject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Entity
public class DayTimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String day;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "day_time_table_id")
    private List<Subject> subjects;

    public DayTimeTable(String day){
        this.subjects=null;
        this.day = day;
    }

    public void addDayTimeTable(Subject subject){
        subjects.add(subject);
    }

    public String getDay() {
        return day;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }
}

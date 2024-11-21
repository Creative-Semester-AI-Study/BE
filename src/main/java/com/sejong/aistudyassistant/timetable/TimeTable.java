package com.sejong.aistudyassistant.timetable;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_table_id")
    private List<DayTimeTable> timeTables;

    public TimeTable() {
        this.timeTables = new ArrayList<>();
        timeTables.add(new DayTimeTable("월"));
        timeTables.add(new DayTimeTable("화"));
        timeTables.add(new DayTimeTable("수"));
        timeTables.add(new DayTimeTable("목"));
        timeTables.add(new DayTimeTable("금"));
    }

    public List<DayTimeTable> getTimeTables() {
        return timeTables;
    }
}

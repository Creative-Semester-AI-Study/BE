package com.sejong.aistudyassistant.timetable;

import com.sejong.aistudyassistant.mypage.MyPage;
import com.sejong.aistudyassistant.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeTableRepository extends JpaRepository<DayTimeTable, Long> {
    Optional<DayTimeTable> findDayTimeTableByDay(String day);

}

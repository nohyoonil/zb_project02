package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Diary;

import java.time.LocalDate;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findAllByDate(LocalDate date);

    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    Diary getFirstByDate(LocalDate date);

    // 다음 클립에서 설명
    @Transactional
    void deleteAllByDate(LocalDate date);
}

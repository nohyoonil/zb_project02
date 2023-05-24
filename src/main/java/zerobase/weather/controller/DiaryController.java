package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        diaryService.createDiary(date, text);
    }

    @ApiOperation("해당 날짜의 일기 조회")
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "날짜 형식 : yyyy-MM-dd", example = "2023-05-05") LocalDate date) {
//        if (date.isAfter(LocalDate.ofYearDay(2023, 1))) {
//            throw new InvalidDate();
//        }
        return diaryService.readDiary(date);
    }

    @ApiOperation("해당 기간의 일기 조회")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return diaryService.readDiaries(startDate, endDate);
    }

    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        diaryService.updateDiary(date, text);
    }

    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        diaryService.deleteDiary(date);
    }
}

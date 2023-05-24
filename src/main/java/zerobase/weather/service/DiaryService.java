package zerobase.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;

    @Value("${openweathermap.key}")
    private String apiKey;


    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherData() {
        dateWeatherRepository.save(getWeatherFromApi());
    }

    public void createDiary(LocalDate date, String text) {
        log.info("created diary of text : " + text);
        DateWeather nowDateWeather = getDateWeather(date);

        Diary nowDiary = new Diary();
        nowDiary.setText(text);
        nowDiary.setDate(date);
        nowDiary.setDateWeather(nowDateWeather);
        diaryRepository.save(nowDiary);
    }

    private DateWeather getWeatherFromApi() {
        String weatherData = getWeatherString();
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));
        return dateWeather;
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if (dateWeatherListFromDB.size() > 0) {
            log.info("get dateWeather from db");
            return dateWeatherListFromDB.get(0);
        } else {
            log.info("get dateWeather from api");
            return getWeatherFromApi();
        }
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }

    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
        log.error("deleted");
    }

    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } catch (Exception e) {
            log.error("failed to get weather from OpenWeatherMapApi");
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            log.error("failed to parse weather data, data: " + jsonString);
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;
    }
}

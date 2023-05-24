package zerobase.weather.error;

import lombok.Getter;

@Getter
public class ErrorResponse {
    String errorName;
    String Description;

    public ErrorResponse(String errorName, String description) {
        this.errorName = errorName;
        Description = description;
    }
}

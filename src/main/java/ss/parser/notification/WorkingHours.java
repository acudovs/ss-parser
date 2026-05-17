package ss.parser.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class WorkingHours {
    private LocalTime start;
    private LocalTime end;

    @JsonIgnore
    public boolean isActive() {
        if (start == null || end == null) return true;
        LocalTime now = LocalTime.now();
        if (start.isBefore(end)) {
            return !now.isBefore(start) && now.isBefore(end);
        } else {
            return !now.isBefore(start) || now.isBefore(end);
        }
    }
}

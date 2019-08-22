package templeofmaat.judgment.data;

import androidx.room.TypeConverter;

import java.time.Instant;

public class Converters {

    @TypeConverter
    public static Instant fromTimestamp(Long value) {
        return value == null ? null : Instant.ofEpochSecond(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Instant instant) {
        return instant == null ? null : instant.getEpochSecond();
    }

}

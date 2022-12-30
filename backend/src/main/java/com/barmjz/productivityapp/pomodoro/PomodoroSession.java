package com.barmjz.productivityapp.pomodoro;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class PomodoroSession extends Pomodoro {
    @ColumnDefault("true")
    private boolean isStudying;
    @ColumnDefault("false")
    private boolean isPaused;
    private long startTime;
    private long remainingTimeInSecs;
}
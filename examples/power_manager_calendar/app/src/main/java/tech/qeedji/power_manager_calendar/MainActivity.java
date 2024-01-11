package tech.qeedji.power_manager_calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Paris"));

        // Schedule when the device will be rebooted.
        ZonedDateTime nextRebootRun = now.withHour(6).withMinute(0).withSecond(0);
        if(now.compareTo(nextRebootRun) > 0)
            nextRebootRun = nextRebootRun.plusDays(1);
        Duration duration = Duration.between(now, nextRebootRun);
        long rebootDelay = duration.getSeconds();
        Context context = getApplicationContext();
        ScheduledExecutorService rebootScheduler = Executors.newScheduledThreadPool(1);
        rebootScheduler.scheduleAtFixedRate(new RebootTask(context),
                rebootDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);

        // Schedule at 10:00AM when the screen will be in sleep mode.
        ZonedDateTime nextGoToSleepRun = now.withHour(10).withMinute(0).withSecond(0);
        if(now.compareTo(nextGoToSleepRun) > 0)
            nextGoToSleepRun = nextGoToSleepRun.plusDays(1);
        long goToSleepDelay = Duration.between(now, nextGoToSleepRun).getSeconds();
        ScheduledExecutorService goToSleepScheduler = Executors.newScheduledThreadPool(1);
        goToSleepScheduler.scheduleAtFixedRate(new PowerManagerTask(context, false),
                goToSleepDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);

        // Schedule at 10:15AM when the screen will be waken up.
        ZonedDateTime nextWakeUpRun = now.withHour(10).withMinute(15).withSecond(0);
        if(now.compareTo(nextWakeUpRun) > 0)
            nextWakeUpRun = nextWakeUpRun.plusDays(1);
        long wakeUpDelay = Duration.between(now, nextWakeUpRun).getSeconds();
        ScheduledExecutorService wakeUpScheduler = Executors.newScheduledThreadPool(1);
        wakeUpScheduler.scheduleAtFixedRate(new PowerManagerTask(context, true),
                wakeUpDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS);
    }
}
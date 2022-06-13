package domain;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.ThreadLocalRandom;

public class Clock extends Thread{
    private Integer delay;
    private LocalTime time;
    public Clock(int delay, LocalTime time){
        this.delay = delay;
        this.time = time;
    }

    public LocalTime getTime() {
        return time;
    }

    public long timeOnMs() {
        return time.toSecondOfDay() * 1000L + time.getNano() / 1000;
    }

    public void increase(long ms){
       System.out.println("Increasing clock in: " + ms + " milliseconds");
        time = time.plus(ms, ChronoUnit.MILLIS);
    }

    public void decrease(long ms){
       System.out.println("Decreasing clock in: " + ms + " milliseconds");
        time = time.minus(ms, ChronoUnit.MILLIS);
    }

    @Override
    public void run() {
        System.out.println("Starting clock at time: " + time);
        while (true){
            try {
                Thread.sleep(1000);
                int randomNum = ThreadLocalRandom.current().nextInt(1, 10);
                if (randomNum % 3==0){
                    Thread.sleep((long) delay);
                }
                time = time.plus(1000, ChronoUnit.MILLIS);
               System.out.println("Current Clock: " + time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

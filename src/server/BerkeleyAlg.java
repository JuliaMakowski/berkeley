package server;

import domain.Clock;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BerkeleyAlg {
    public Map<String, Long> runAlg(Map<String, NodeReferenceTime> nodeReferenceTimeMap, long processTime) {
        long mainServerTime = Clock.timeToMs(nodeReferenceTimeMap.get("SERVER").getNodeTime());
        long summedTimes = nodeReferenceTimeMap.values()
                .stream()
                .map(NodeReferenceTime::getNodeTime)
                .map(Clock::timeToMs)
                .reduce(0L, Long::sum);

        long average = Math.round(summedTimes * 1.0 / nodeReferenceTimeMap.size());
        List<Long> filteredValues = nodeReferenceTimeMap.values().stream()
                .map(NodeReferenceTime::getNodeTime)
                .peek(it -> System.out.println("All hours: " + it))
                .map(Clock::timeToMs)
                .filter(time -> is10SecondsApart(average, time))
                .collect(Collectors.toList());

        if (filteredValues.size() == 0) filteredValues.add(mainServerTime);
        long sumFiltered = filteredValues.stream().reduce(0L, Long::sum);
        long avgFiltered = Math.round(sumFiltered * 1.0 / filteredValues.size());
        System.out.println("\n\n");
        Map<String, Long> resultMap = nodeReferenceTimeMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    NodeReferenceTime referenceTime = e.getValue();
                    LocalTime nodeTime = referenceTime.getNodeTime();
                    long nodeTimeMs = Clock.timeToMs(nodeTime, true);
                    System.out.println("node: *" + e.getKey() +  "* NodeTime: (" + nodeTime  + ") AVG: (" + Clock.toFormattedHour(avgFiltered) + ") RTT: " + referenceTime.getRTT());
                    System.out.println("node: *" + e.getKey() +  "* NodeTime: (" + nodeTimeMs  + ") AVG: (" + avgFiltered + ")");
                    long difference = avgFiltered - nodeTimeMs;
                    long halfOfRtt = referenceTime.getRTT() / 2;
                    long result = difference + halfOfRtt + processTime;
                    System.out.println("Difference: " + difference + " halfOFRTT: " + halfOfRtt + " processTime: " + processTime);
                    System.out.println("Change to: " + result + "  to be: from: " + nodeTime + " to: " + operate(nodeTime, result));
                    System.out.println("-------------------------------------------------------------------");
                    return result;
                }));

        System.out.println("\n\n");
        return resultMap;
        /*
        java server.MainNode host 10:00:00.000 10 0 0
        java client.Client 4 host 2004 11:00:05.000 0 0
        java client.Client 2 host 2002 10:00:05.000 0 0
        java client.Client 1 host 2001 10:00:05.000 0 0
        java client.Client 3 host 2000 10:00:05.000 0 0
         */
    }

    private boolean is10SecondsApart(long average, Long time) {
        return (average + 5000) > time && time > (average - 5000);
    }

    private LocalTime operate(LocalTime toChange, long howMuch) {
        if (howMuch < 0) {
            return toChange.minus(Math.abs(howMuch), ChronoUnit.MILLIS);
        } else {
            return toChange.plus(Math.abs(howMuch), ChronoUnit.MILLIS);
        }
    }
}

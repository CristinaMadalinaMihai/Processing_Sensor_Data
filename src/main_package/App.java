package main_package;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class App {

    private static void writeToFile(String stringToPrint, int taskNumber){
        try { // write into the file
            FileWriter myWriter = new FileWriter("Task_" + taskNumber + ".txt");
            myWriter.write(stringToPrint);
            myWriter.close();
            System.out.println("Successfully wrote to the file " + taskNumber);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {

        String fileName = "Activities.txt";
        List<String> list = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            list = stream.collect(toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        /**************** TASK 1 ***************************************************************/
        List<MonitoredData> finalList = list.stream().map(s -> {
            String[] splitted_parts = s.split("\\t+");
            return new MonitoredData(splitted_parts[0], splitted_parts[1], splitted_parts[2]);
        }).collect(Collectors.toList());
        String toPrintTask1 = new String();
        for (MonitoredData data : finalList) {
            toPrintTask1 = toPrintTask1 + data.getStartTime() + "\t\t" + data.getEndTime() + "\t\t" + data.getActivity() + "\n";
        }
        writeToFile(toPrintTask1, 1);

        /**************** TASK 2 ***************************************************************/
        List<String> days = finalList.stream().map(s -> {
            return s.getStartTime().split("\\s+")[0];
        }).collect(Collectors.toUnmodifiableList());
        List<String> distinctDays = days.stream().distinct().collect(toList());
        String toPrintTask2 = Integer.toString(distinctDays.size());
        writeToFile(toPrintTask2, 2);

        /**************** TASK 3 ***************************************************************/
        List<String> activities = finalList.stream().map(s -> {
            return s.getActivity();
        }).collect(Collectors.toUnmodifiableList());
        List<String> distinctActivities = activities.stream().distinct().collect(toList());
        Map<String, Integer> activityList = new HashMap<String, Integer>();
        for (String iterator : distinctActivities) {
            long counter = activities.stream().filter(a -> {
                return a.equals(iterator);
            }).count();
            activityList.put(iterator, (int) counter);
        }
        String toPrintTask3 = new String();
        for (Map.Entry<String, Integer> entry : activityList.entrySet()) {
            toPrintTask3 = toPrintTask3 + entry.getKey() + " " + entry.getValue() + "\n";
        }
        writeToFile(toPrintTask3, 3);

        /**************** TASK 4 ***************************************************************/
        Map<Integer, Map<String, Integer>> activitiesPerDay = new HashMap<Integer, Map<String, Integer>>();
        for (String currentDay : distinctDays) {
            List<MonitoredData> currentDayData = finalList.stream().filter(a -> {
                String startTime = a.getStartTime().split("\\s+")[0];
                String endTime = a.getEndTime().split("\\s+")[0];
                if (startTime.equals(currentDay) || endTime.equals(currentDay))
                    return true;
                else
                    return false;
            }).collect(toList());
            List<String> currentDayActivities = new ArrayList<String>();
            for (MonitoredData data : currentDayData) {
                currentDayActivities.add(data.getActivity());
            }
            List<String> currentDayDistinctActivities = currentDayActivities.stream().distinct().collect(toList());
            Map<String, Integer> currentDayActivityList = new HashMap<String, Integer>();
            for (String iterator : currentDayDistinctActivities) {
                long counter = currentDayActivities.stream().filter(a -> {
                    return a.equals(iterator);
                }).count();
                currentDayActivityList.put(iterator, (int) counter);
            }
            activitiesPerDay.put(Integer.parseInt(currentDay.split("-")[2]), currentDayActivityList);
        }
        String toPrintTask4 = new String();
        for (Map.Entry<Integer, Map<String, Integer>> entry : activitiesPerDay.entrySet()) {
            toPrintTask4 = toPrintTask4 + "Day: " + entry.getKey() + "\n";
            for (Map.Entry<String, Integer> entry1 : entry.getValue().entrySet()) {
                toPrintTask4 = toPrintTask4 + entry1.getKey() + " " + entry1.getValue() + "\n";
            }
            toPrintTask4 = toPrintTask4 + "\n";
        }

        /**************** TASK 5 ***************************************************************/
        Map<String, LocalDateTime> entireDuration = new HashMap<String, LocalDateTime>();
        for (String currentActivity : distinctActivities) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int i = 0;
            List<LocalDateTime> durationn = finalList.stream().map(a -> {
                String strr = "0001-01-01 00:00:00";
                LocalDateTime durationnn = LocalDateTime.parse(strr, formatter);
                for (MonitoredData data : finalList) {
                    if (data.getActivity().equals(currentActivity)) {
                        LocalDateTime startTime = LocalDateTime.parse(data.getStartTime(), formatter);
                        LocalDateTime endTime = LocalDateTime.parse(data.getEndTime(), formatter);
                        long dayss = startTime.until( endTime, ChronoUnit.DAYS );
                        startTime = startTime.plusDays( dayss );
                        long hours = startTime.until( endTime, ChronoUnit.HOURS );
                        startTime = startTime.plusHours( hours );
                        long minutes = startTime.until( endTime, ChronoUnit.MINUTES );
                        startTime = startTime.plusMinutes( minutes );
                        long seconds = startTime.until( endTime, ChronoUnit.SECONDS );
                        durationnn = durationnn.plusDays(dayss);
                        durationnn = durationnn.plusHours(hours);
                        durationnn = durationnn.plusMinutes(minutes);
                        durationnn = durationnn.plusSeconds(seconds);
                    }
                }
                return durationnn;
            }).collect(Collectors.toList());
            entireDuration.put(currentActivity, durationn.get(i));
            i++;
        }
        String toPrintTask5 = new String();
        for (Map.Entry<String, LocalDateTime> entry : entireDuration.entrySet()) {
            toPrintTask5 = toPrintTask5 + entry.getKey() + " "  + (entry.getValue().getDayOfMonth() - 1) + " day(s) "
                    + entry.getValue().getHour() + ":" + entry.getValue().getMinute() + ":" + entry.getValue().getSecond() + "\n";
        }
        toPrintTask5 = toPrintTask5 + "\n";
        writeToFile(toPrintTask5, 5);

        /**************** TASK 6 ***************************************************************/
        Map<String, Long> activitiesOccurrences = finalList.stream().filter(a -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(a.getStartTime(), formatter);
            LocalDateTime endTime = LocalDateTime.parse(a.getEndTime(), formatter);
            String str = "0001-01-01 00:00:00";
            LocalDateTime duration = LocalDateTime.parse(str, formatter);
            long hours = startTime.until(endTime, ChronoUnit.HOURS);
            startTime = startTime.plusHours(hours);
            long minutes = startTime.until(endTime, ChronoUnit.MINUTES);
            startTime = startTime.plusMinutes(minutes);
            long seconds = startTime.until(endTime, ChronoUnit.SECONDS);
            duration = duration.plusHours(hours);
            duration = duration.plusMinutes(minutes);
            duration = duration.plusSeconds(seconds);
            if (duration.getHour() > 0 || duration.getMinute() > 5 || (duration.getMinute() == 5 && duration.getSecond() > 0))
                return false;
            else
                return true;
        }).collect(Collectors.groupingBy(a->a.getActivity(), Collectors.counting()));
        List<String> frequentActivities = new ArrayList<String>();
        for (Map.Entry<String, Long> finalActivity : activitiesOccurrences.entrySet()) {
            for (Map.Entry<String, Integer> currentActivity : activityList.entrySet()) {
                if (finalActivity.getKey().equals(currentActivity.getKey()) && finalActivity.getValue() >= 0.9 * currentActivity.getValue()) {
                    frequentActivities.add(finalActivity.getKey());
                }
            }
        }
        String toPrintTask6 = new String();
        for (String activity : frequentActivities) {
            toPrintTask6 = toPrintTask6 + activity + "\n";
        }
        writeToFile(toPrintTask6, 6);

    }

}

package com.example.weather;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Weather {

    private static Document getPage() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите название города (названия городов пишутся по-английски и с маленькой буквы):");

        String city = scanner.nextLine();

        String url = "http://www.nepogoda.ru/russia/" + city + "/";

        try {
            return Jsoup.parse(new URL(url), 3000);
        } catch (IOException e) {
            throw new Exception("Неправильно введены данные или нет прогноза на этот город");
        }
    }

   private static final Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");

    private static String getDateFromString(String stringDate) throws Exception {
        Matcher matcher = pattern.matcher(stringDate);
        if (matcher.find()){
            return matcher.group();
        }
        throw new Exception("Не удается извлечь данные из строки");
    }

    private static int printPartValues(Elements values, int index){
        int iterationCount = 4;
        if (index == 0){
            Element valueLn = values.get(3);
            boolean isMorning = valueLn.text().contains("Утро");
            boolean isAfternoon = valueLn.text().contains("День");
            boolean isEvening = valueLn.text().contains("Вечер");

            if (isMorning){
                iterationCount = 3;
            } else if (isAfternoon){
                iterationCount = 2;
            } else if (isEvening){
                iterationCount = 5;
            }
        }

        for (int i = 0; i < iterationCount; i++) {
            Element valueLine = values.get(index + i);
            for (Element td : valueLine.select("td")) {
                System.out.print(td.text() + "    ");
            }
            System.out.println();
        }

        return iterationCount;
    }

    public static void main(String[] args) throws Exception {
        Document page = getPage();

        Element tableWth = Objects.requireNonNull(page).select("table[class=wt]").first();
        Elements names = Objects.requireNonNull(tableWth).select("tr[class=wth]");
        Elements values = tableWth.select("tr[valign=top]");
        int index = 0;
        for (Element name : names){
            String dateString = name.select("th[id=dt]").text();
            String date = getDateFromString(dateString);
            System.out.println(date + "              Явления           Температура  Давл   Влажность     Ветер");
            int iterationCount = printPartValues(values, index);
            index = index + iterationCount;
        }

    }
}
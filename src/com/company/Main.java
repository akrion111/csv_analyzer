package com.company;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {

    public static void printTen(List<List<String>> data, List<String> columns){
        int limit;
        if(data.size()>10) limit=10; else limit=data.size();
        for(int i=0;i<limit;i++)
       {
           for(int j=0;j<columns.size();j++) {
               System.out.println(columns.get(j)+":"+data.get(i).get(j));
           }
           System.out.println("-------------------");
       }
    }

    public static void printNumberOfObjects(List<List<String>> data)
    {
        System.out.println("Number of objects:"+data.size());
    }

    public static void printAllBigger(List<List<String>> data,List<String> columns, int id) throws CsvException {
        int indexOfIdColumn=columns.indexOf("id");
        if(indexOfIdColumn==-1)throw new CsvException("no column with the name:id");
        else  {
            data.stream().sorted(Comparator.comparingInt(e-> Integer.parseInt(e.get(indexOfIdColumn))))
                         .filter(idHigherThan(id,indexOfIdColumn)).forEach(System.out::println);
        }
    }

    public static Predicate<List<String>> idHigherThan(int id,int indexOfIdColumn) {
        return p -> Integer.parseInt(p.get(indexOfIdColumn))>id;
    }

    public static void printNamesAndOccurrences(List<List<String>> data, List<String> columns) throws CsvException {
        int indexOfNameColumn = columns.indexOf("name");
        if (indexOfNameColumn == -1) throw new CsvException("no column with the name:name");
        else {
            HashMap<String, Integer> result = new HashMap<>();
            List<String> names = data.stream().map(e -> e.get(indexOfNameColumn)).collect(Collectors.toList());
            for (String name : names) {
                if (!result.containsKey(name)) result.put(name, 1);
                else result.put(name, result.get(name) + 1);
            }
            result.entrySet().stream().sorted(Map.Entry.comparingByValue()).filter(item-> item.getValue() >100).forEach(item -> System.out.println(item.getKey()+ " "+item.getValue()));
        }
    }


    public static void printNameAndCountParents(List<List<String>> data,List<String> columns,int id) throws CsvException {
        int indexOfNameColumn=columns.indexOf("name");
        int indexOfParentIdColumn=columns.indexOf("parent_id");
        int numberOfParents=0;
        String name;
        if (indexOfNameColumn == -1) throw new CsvException("no column with the name:name");
        if(id<1) throw new CsvException("missing or wrong id!!!");
        else name=data.get(id-1).get(indexOfNameColumn);
        if (indexOfParentIdColumn == -1) throw new CsvException("no column with the name:parent_id");
            else
        {
            int parentId=Integer.parseInt(data.get(id-1).get(indexOfParentIdColumn));
            while(parentId!=0&&Integer.parseInt(data.get(parentId-1).get(indexOfParentIdColumn))!=parentId)
            {
                parentId=Integer.parseInt(data.get(parentId-1).get(indexOfParentIdColumn));
                numberOfParents++;
            }
        }
        System.out.println(name+" - "+numberOfParents);
    }

    public static void printMediumAge(List<List<String>> data, List<String> columns) throws CsvException {
        int indexOfAgeColumn=columns.indexOf("age");
        if (indexOfAgeColumn == -1) throw new CsvException("no column with the name:age");
        else {
            double mediumValue = data.stream().map(e -> e.get(indexOfAgeColumn)).collect(Collectors.averagingInt(num -> Integer.parseInt(num)));
            System.out.println("Medium age:" + String.format("%.2f", mediumValue));
        }
    }


    public static void printRangesOfAge(List<List<String>> data, List<String> columns) throws CsvException {
        int indexOfAgeColumn=columns.indexOf("age");
        if (indexOfAgeColumn == -1) throw new CsvException("no column with the name:age");
        else {
            int[] ages = data.stream().mapToInt(e -> Integer.parseInt(e.get(indexOfAgeColumn))).sorted().toArray();
            Double max = data.stream().map(e -> Double.parseDouble(e.get(indexOfAgeColumn))).max(Double::compare).get();
            Double min = data.stream().map(e -> Double.parseDouble(e.get(indexOfAgeColumn))).min(Double::compare).get();
            Double current = min;
            double[] occurrences = new double[4];
            Double step = (max - min) / 4;
            System.out.println("max:" + max);
            System.out.println("min:" + min);
            System.out.println("25%:" + (max - min) / 4);
            for (int i = 0; i < 4; i++) {
                occurrences[i] = getPercentOfAllElementsInRange(ages, current, current + step);
                System.out.println(current + "-" + (current + step) + " :" + (occurrences[i] + "%"));
                current += step;
            }
        }
    }
    static double getPercentOfAllElementsInRange(int[] ages,Double a,Double b){
        return  (double)Arrays.stream(ages).filter(x->(a<x&&x<=b)).count()*100/ages.length;
    }

    public static void main(String[] args) {
        List<String> arguments= new ArrayList<>(Arrays.asList(args));
        List<List<String>>data;
        String fileName=args[0];
        int numberOfOperation=Integer.parseInt(args[1]);
        int additionalParameter=0;
        if(args.length>2) additionalParameter=Integer.parseInt(args[2]);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            List<String> columns=Arrays.asList(reader.readLine().split(","));
            data=reader.lines().map(e->Arrays.asList(e.split(","))).collect(Collectors.toList());

            switch (numberOfOperation)
            {
                case 1: printTen(data,columns);
                    break;
                case 2: printNumberOfObjects(data);
                    break;
                case 3: printAllBigger(data,columns,additionalParameter);
                    break;
                case 4: printNamesAndOccurrences(data,columns);
                    break;
                case 5: printNameAndCountParents(data,columns,additionalParameter);
                    break;
                case 6: printMediumAge(data,columns);
                    break;
                case 7: printRangesOfAge(data,columns);
                    break;
                default:System.out.println("Choose operation from 1 to 7...");break;

            }


        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

    }
}

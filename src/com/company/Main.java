package com.company;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {


    public static void printTen(HashMap<String, List<String>> data, List<String> columns) {
        int size=0;
        for(String item:data.keySet())
        {
            for(int j=0;j<columns.size();j++) {
                System.out.println(columns.get(j)+":"+data.get(item).get(j));
            }
            System.out.println("-------------------");
            size++;
            if(size==10)break;
        }
    }

    public static void printNumberOfObjects(HashMap<String, List<String>> data) {
        System.out.println("Number of objects:"+data.size());
    }

    public static void printAllBigger(HashMap<String,List<String>> data,List<String> columns, int id) throws CsvException {
        int indexOfIdColumn=columns.indexOf("id");
        if(indexOfIdColumn==-1)throw new CsvException("no column with the name:id");
        else  {
            data.values().stream().sorted(Comparator.comparingInt(e-> Integer.parseInt(e.get(indexOfIdColumn))))
                         .filter(idHigherThan(id,indexOfIdColumn)).forEach(System.out::println);
        }
    }

    public static Predicate<List<String>> idHigherThan(int id,int indexOfIdColumn) {
        return p -> Integer.parseInt(p.get(indexOfIdColumn))>id;
    }


    public static void printNamesAndOccurrences(HashMap<String,List<String>> data, List<String> columns) throws CsvException {
        int indexOfNameColumn = columns.indexOf("name");
        if (indexOfNameColumn == -1) throw new CsvException("no column with the name:name");
        else {
            HashMap<String, Integer> result = new HashMap<>();
            List<String> names=data.values().stream().map(e -> e.get(indexOfNameColumn)).collect(Collectors.toList());
            for (String name : names) {
                if (!result.containsKey(name)) result.put(name, 1);
                else result.put(name, result.get(name) + 1);
            }
            result.entrySet().stream().sorted(Map.Entry.comparingByValue()).filter(item-> item.getValue() >100).forEach(item -> System.out.println(item.getKey()+ " "+item.getValue()));
        }
    }




    public static void printNameAndCountParents(HashMap<String, List<String>> data, List<String> columns, String id) throws CsvException {
        int indexOfNameColumn=columns.indexOf("name");
        int indexOfParentIdColumn=columns.indexOf("parent_id");
        int numberOfParents=0;
        String name;
        if (indexOfNameColumn == -1) throw new CsvException("no column with the name:name");
        if(!data.containsKey(id)) throw new CsvException("missing or wrong id!!!");
        else name=data.get(id).get(indexOfNameColumn);
        if (indexOfParentIdColumn == -1) throw new CsvException("no column with the name:parent_id");

        else
        {
            int parentId;
            if(data.get(id).get(indexOfParentIdColumn).equals(""))throw new CsvException("no parent_id value");
            else {
                parentId = Integer.parseInt(data.get(id).get(indexOfParentIdColumn));
            }
            while(parentId!=0&&Integer.parseInt(data.get(String.valueOf(parentId)).get(indexOfParentIdColumn))!=parentId)
            {
                parentId=Integer.parseInt(data.get(String.valueOf(parentId)).get(indexOfParentIdColumn));
                numberOfParents++;
            }
        }
        System.out.println(name+" - "+numberOfParents);
    }

    public static void printMediumAge(HashMap<String,List<String>> data, List<String> columns) throws CsvException {
        int indexOfAgeColumn=columns.indexOf("age");
        if (indexOfAgeColumn == -1) throw new CsvException("no column with the name:age");
        else {
            double mediumValue=data.values().stream().map(e -> e.get(indexOfAgeColumn)).collect(Collectors.averagingInt(Integer::parseInt));
            System.out.println("Medium age:" + String.format("%.2f", mediumValue));
        }
    }


    public static void printRangesOfAge(HashMap<String,List<String>> data, List<String> columns) throws CsvException {
        int indexOfAgeColumn=columns.indexOf("age");
        if (indexOfAgeColumn == -1) throw new CsvException("no column with the name:age");
        else {
            int[] ages=data.values().stream().mapToInt(e -> Integer.parseInt(e.get(indexOfAgeColumn))).sorted().toArray();
            Double max = data.values().stream().map(e -> Double.parseDouble(e.get(indexOfAgeColumn))).max(Double::compare).get();
            Double min = data.values().stream().map(e -> Double.parseDouble(e.get(indexOfAgeColumn))).min(Double::compare).get();
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
        HashMap<String,List<String>>data=new HashMap<>();
        String fileName=args[0];
        int numberOfOperation=Integer.parseInt(args[1]);
        int additionalParameter=0;
        if(args.length>2) additionalParameter=Integer.parseInt(args[2]);
        String currentId;
        int indexOfIdColumn;
        String line;
        String DELIMITER=",";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            List<String> columns=Arrays.asList(reader.readLine().split(","));
            indexOfIdColumn=columns.indexOf("id");
            while((line=reader.readLine())!=null)
            {
                currentId=line.split(",")[indexOfIdColumn];
                data.put(currentId,Arrays.asList(line.split(DELIMITER, -1)));
            }
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
                case 5: printNameAndCountParents(data,columns,String.valueOf(additionalParameter));
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

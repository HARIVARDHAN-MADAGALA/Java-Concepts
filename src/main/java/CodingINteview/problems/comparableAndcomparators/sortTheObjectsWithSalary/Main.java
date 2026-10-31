package CodingINteview.problems.comparableAndcomparators.sortTheObjectsWithSalary;

import java.util.*;

public class Main {
    public static void main(String[] args) {


        Employee hari = new Employee("hari",5);
        Employee kunal = new Employee("kunal",25);
        Employee kaveri = new Employee("kaveri",15);
        Employee john = new Employee("john",2);

        List<Employee> list = new ArrayList<>(Arrays.asList(hari,kunal,kaveri,john));

        list.sort(Comparator.comparing( ( a) -> a.salary));
        list.sort(Comparator.comparing( ( Employee a) -> a.salary).reversed());


        Collections.sort(list,Comparator.comparing((a) -> a.salary));
        Collections.sort(list,Comparator.comparing((Employee a) -> a.salary).reversed());

        list.sort((a,b) -> Integer.compare(b.getSalary(), a.getSalary()));
        Collections.sort(list,(a,b) -> Integer.compare(b.getSalary(), a.getSalary()));



        System.out.println(list);


        //Normal sorting

//        Collections.sort(list);
//        Collections.sort(list, Collections.reverseOrder());
//--------------------------------------------------------
//        List<String> sorted = list.stream()
//                .sorted()
//                .collect(Collectors.toList());

//        List<String> sorted = list.stream()
//                .sorted(Comparator.reverseOrder())
//                .collect(Collectors.toList());
//------------------------------------------------------------
    }
}

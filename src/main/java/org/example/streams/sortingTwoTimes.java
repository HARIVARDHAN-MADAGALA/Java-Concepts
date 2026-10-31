package org.example.streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class sortingTwoTimes {

    private String name;

    private  Integer age;

    private  Integer salary;

    public sortingTwoTimes(String name, Integer age, Integer salary) {
        this.name = name;
        this.age = age;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "sortingTwoTimes{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", salary=" + salary +
                '}';
    }

    public static void main(String[] args) {


        List<sortingTwoTimes> list = new ArrayList<>();

        sortingTwoTimes obj1 = new sortingTwoTimes("fikke",24,5000);
        sortingTwoTimes obj2 = new sortingTwoTimes("efeef",32,1000);
        sortingTwoTimes obj3 = new sortingTwoTimes("veas",42,8000);

        list.add(obj1);list.add(obj2);list.add(obj3);

        System.out.println(

                list.stream()
                        .sorted(Comparator.comparing((sortingTwoTimes c) -> c.getSalary()).reversed()
                                .thenComparing(c -> c.getAge()))
                        .collect(Collectors.toList())
        );
    }
}

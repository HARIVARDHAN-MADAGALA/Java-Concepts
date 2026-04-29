package org.example.Design_patterns.builder;

public class Student {

    //required fields
    private int rollno;
    private String name;

    //optional fields
    private String age;
    private String department;
    private String college;
    private String phone;

    private Student(StringBuilder stringBuilder){

        this.rollno = stringBuilder.rollno;
        this.name = stringBuilder.name;
        this.age = stringBuilder.age;
        this.department = stringBuilder.department;
        this.college = stringBuilder.college;
        this.phone = stringBuilder.phone;
    }


    public static class StringBuilder{

        //required fields
        private int rollno;
        private String name;

        //optional fields
        private String age;
        private String department;
        private String college;
        private String phone;

        public StringBuilder(int rollno, String name){
            this.rollno = rollno;
            this.name = name;
        }

        public StringBuilder age(String age){
            this.age = age;
            return this;
        }
        public StringBuilder department(String department){
            this.department = department;
            return this;
        }
        public StringBuilder college(String college){
            this.college = college;
            return this;
        }
        public StringBuilder phone(String phone){
            this.phone = phone;
            return this;
        }

        public Student build(){
            return new Student(this);
        }
    }

    @Override
    public String toString() {
        return "Student{" +
                "rollno=" + rollno +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", department='" + department + '\'' +
                ", college='" + college + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

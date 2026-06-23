package org.example.problems.math;


public class Amstrong {

    public boolean IsAmstrong (int num) {

        int count = 0;
        int mocknum = num;
        while (mocknum != 0) {
            mocknum = mocknum / 10;
            count++;
        }
        double sum = 0;
        int dem = 1;
        for (int i = 1; i <= count; i++) {

            int digit = (num / dem) % 10;
            sum = Math.pow(digit, count) + sum;
            dem = dem * 10;
        }

        return sum == num;
    }

//    Alternative :
//    public boolean isArmstrong(int number) {
//        int original = number, sum = 0;
//        int digits = String.valueOf(number).length();
//        while (number > 0) {
//            int digit = number % 10;
//            sum += Math.pow(digit, digits);
//            number /= 10;
//        }
//        return sum == original;
//    }

    public static void main(String[] args) {

        Amstrong k = new Amstrong();
        System.out.println(k.IsAmstrong(1634));
    }
}


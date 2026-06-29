package CodingINteview.problems;

public class reverse_number {

    public static void main(String[] args) {

        int num = 1234567;
//        StringBuilder s = new StringBuilder(String.valueOf(num));
//        System.out.println(Integer.valueOf(s.reverse().toString()));
//
        int reverse = 0;

        while(num!=0) {

            int reminder = num % 10;
            reverse = reverse*10  + reminder;
            num = num/10;
        }

        System.out.println(reverse);

    }
}


package CodingINteview.problems.strings;

public class remove_all_whitespaces_from_string {


    public String removeWhitespaces(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) != ' ') {
                result.append(input.charAt(i));
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {

        remove_all_whitespaces_from_string k = new remove_all_whitespaces_from_string();
        System.out.println(k.removeWhitespaces("sdc dc dc"));
    }
}


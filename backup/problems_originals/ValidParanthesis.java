package CodingINteview.problems.stacks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ValidParanthesis {

    public static boolean method ( String word){

        boolean isValid;

        Map<String,String> map = new HashMap<>();

        map.put(")","(");
        map.put("}","{");
        map.put("]","[");

        Stack<String> stack = new Stack<>();

        for( String sym : word.split("")){

            if( sym.equals("(") || sym.equals("{") || sym.equals("[")){

                stack.push(sym);
            }

            if( sym.equals(")") || sym.equals("}") || sym.equals("]")){

                if(stack.empty()) return false;
                if ( !stack.peek().equals(map.get(sym))){return false;}
                stack.pop();
            }
        }
        return stack.empty();
    }

    public static void main(String[] args) {

        String word = "()[]{}";

        System.out.println(

                method(word)
        );
    }
}


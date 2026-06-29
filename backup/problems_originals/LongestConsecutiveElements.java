package CodingINteview.problems.hashing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class LongestConsecutiveElements {

    public static int longestConsecutiveElements(List<Integer> list){

        HashSet<Integer> set = new HashSet<>();

        // Step 1: Insert everything

        for ( Integer i : list){
            set.add(i);
        }

        int currlenght = 1;
        int MaxLenght = 0;

        // Step 2: Traverse set

        for(Integer i : list){

            // Only start if it's beginning of sequence
            if( !set.contains(i-1) ){

                while(set.contains(i+1)){

                    currlenght++;
                    i++;
                }
            }

            MaxLenght = Math.max(MaxLenght,currlenght);
            currlenght =1;
        }

        return MaxLenght;
    }

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(100, 4, 200, 1, 3, 2);

        System.out.println(longestConsecutiveElements(list));
    }
}


///🔴 Problem 1: You are iterating over list, not set
/// for(Integer i : list)
///
/// This is a mistake.
///
/// If list has duplicates, your logic may run multiple times unnecessarily.
///
/// You already built a HashSet.
/// So iterate over set, not list.
///
/// 🔴 Problem 2: You are modifying i inside loop
/// while(set.contains(i+1)){
///     currlenght++;
///     i++;
/// }
///
/// This is dangerous.
///
/// i in enhanced for-loop is just a copy.
/// Changing it does NOT update the loop iteration variable.
///
/// This works accidentally but is bad practice and confusing.
///
/// Better:
///
/// int current = i;
/// while(set.contains(current + 1)) {
///     current++;
///     currlenght++;
/// }
///
/// Never modify loop variable.
///
/// 🔴 Problem 3: Missing Early Continue
///
/// Right now:
///
/// if (!set.contains(i - 1)) {
///     // expand
/// }
/// MaxLength = ...
///
/// Even if it's NOT a start of sequence,
/// you still update max.
///
/// Better structure:
///
/// for(Integer num : set) {
///
///     if (!set.contains(num - 1)) {
///
///         int current = num;
///         int currLength = 1;
///
///         while(set.contains(current + 1)) {
///             current++;
///             currLength++;
///         }
///
///         maxLength = Math.max(maxLength, currLength);
///     }
/// }
///
/// Cleaner. No confusion.
///
/// 🔴 Problem 4: Edge Case Missing
///
/// If list is empty:
///
/// Your code returns 0?
/// Actually it returns 0 because maxLength = 0.
///
/// That’s correct. Good.
///
/// But you didn’t explicitly check.
///
/// In interviews, clarity matters.

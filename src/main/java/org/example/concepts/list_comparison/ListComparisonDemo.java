package org.example.concepts.list_comparison;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <h1>Comprehensive Guide to List Creation Strategies in Java</h1>
 * <p>
 * This class provides  teractive demonstration and explanation of the critical differences
 * between four commo ng lists in Java:
 * 
 * </p> 
 * <ol>
 * 
 *     <li><b>Normal ArrayList Creation:</b> {@code new ArrayList<>()}</li>
 *     <li><b>Array Wrapper List:</b> {@code Arrays.asList()}</li>
 *     <li><b>Immutable/Unmodifiable List (Java 9+):</b> {@code List.of()}</li>
 *     <li><b>Unmodifiable View Wrapper:</b>

        
 * <h2>Summary Comparison Matrix</h2>
 * <table border="1" cellpadding="5" cellspacing="0" style="border-collapse: collapse; text-align: left;">
 *     <tr style="background-color: #f2f2f2;">
 *         <th>Feature / Property</th>
 *         <th>{@code new ArrayList<>()}</th>
 *         <th>{@code Arra asList()}</th>
 *         <th>{@code List.of()}</th>
 *         <th>{@code Collections.unmodifiableList()}</th>
 *     </tr>
 *     <tr>
 *         <td><b>Concrete Implementation</b></td>
 *         <td>{@code java .util.Ar rayList}</td>
 *         <td>{@code java.util.Arrays$ArrayList} (priva

           <td>{@code java.util.Collections$UnmodifiableList} (wrapper)</td>
 *     </tr>

           <td><b>Structural Modification</b><br>(add/remove/clear)</td>
        // 
 *         <td><font color="green"><b>Allowed</b></font></td>
 *         <td><font color="red"><b>Forbidden</b></font><br>(Throws {@code UnsupportedOperationException})</td>
 *         <td><font color="red"><b>Forbidden</b></font><br>(Throws {@code UnsupportedOperationException})</td>
 *         <td><font color="red"><b>Forbidden</b> via view</font><br>(Throws {@code UnsupportedOperationException})</td>
 *     </tr>

           <td><b>Value Modifi on</b><br>(set element at index)</td>
 *         <td><font color="green"><b>Allowed</b></font></td>
 *         <td><font color="gr ><b>Allowed</b></font><br>(Modifies backing array!)</td>
 *         <td><font color="red"><b>Forbidden</b></font><br>(Throws {@code UnsupportedOperationException})</td>
 *         <td><font color="red"><b>Forbidden</b> via view</font><br>(Throws {@code UnsupportedOperationException})</td>
 *     </tr>
 *     <tr>
 *         <td><b>Resizable?</b></td>
 *         <td><font color="green"><b>Yes</b></font></td>
 *         <td><font color

           <td><font color="red"><b>No</b></font> (Fixed to size of backing list)</td>
 *     </tr>
        // 

           <td><b>Null Elements Allowed?</b></td>
 *         <td><font color="green"><b>Yes</b></font></td>
 *         <td><font color="green"><b>Yes</b></font></td>
 *         <td><font color="red"><b>No</b></font><br>(Throws {@code NullPointerException})</td>
 *         <td><b>Depends</b> on backing list (Yes if backing allows it)</td>
 *     </tr>
 *     <tr>
 *         <td><b>Reflects Backing Changes?</b></td>
 *         <td>N/A (Is independent)</td>
 *         <td><font color="green"><b>Yes</b></font><br>(Two-way connection with backing array)</td>
 *         <td>N/A (Copies elements upon creation)</td>
 *         <td><font color="green"><b>Yes</b></font><br>(One-way connection: changes in backing list reflect here)</td>
 *     </tr>
 *     <tr>
 *         <td><b>Memory Efficiency</b></td>
 *         <td>Standard overhead (contains empty space for growth)</td>
 *         <td>Very high (just wraps an existing array)</td>
 *         <td>Extremely high (optimized representation, no extra array)</td>
 *         <td>Low overhead (thin wrapper around another list)</td>
 *     </tr>
 * </table>
 * 
 * <h2>Key Takeaways & Deep Dive</h2>
 * <ul>
 *     <li>
 *         <b>{@code new ArrayList<>()}</b> creates a brand new, empty, fully mutable resizable list. 
 *         It is the standard choice when you plan to build or modify a list dynamically.
 *     </li>
 *     <li>
 *         <b>{@code Arrays.asList()}</b> produces a <i>bridge</i> view between arrays and collections. 
 *         Since it is backed by an array, the size is fixed. You can change elements ({@code set(i, x)}) 
 *         but cannot grow or shrink the list. Changes "write through" in both directions.
 *     </li>
 *     <li>
 *         <b>{@code List.of()}</b> (introduced in Java 9) creates a truly <i>immutable</i> collection. 
 *         It does not allow changes of any kind (neither structural nor content updates). 
 *         It is highly optimized, null-hostile, and thread-safe.
 *     </li>l̥
 *     <li>
 *         <b>{@code Collections.unmodifiableList()}</b> creates a read-only <i>view wrapper</i>. 
 *         It is <b>not</b> a copy of the list. While you cannot modify the list through the wrapper, 
 *         if you still have a reference to the original backing list and modify it, the changes 
 *         <b>will</b> be visible through the unmodifiable wrapper list.
 *     </li>
 * </ul>
 * 
 * @author HARI VARDHAN
 * @version 1.0
 */
public class ListComparisonDemo {

    public static void main(String[] args) {
        printHeader("JAVA LIST CREATION COMPARISON DEMO");

        demoArrayList();
        demoArraysAsList();
        demoListOf();
        demoCollectionsUnmodifiableList();
        demoCrucialViewBehavior();

        printSummaryConclusion();
    }

    /**
     * Demonstrates standard ArrayList creation using new ArrayList<>().
     * Key characteristics: Full mutability, resizable, allows nulls, independent memory.
     */
    private static void demoArrayList() {
        printSubHeader("1. Standard ArrayList: new ArrayList<>()");
        
        // 1. Creation
        List<String> list = new ArrayList<>();
        list.add("Java");
        list.add("Spring");
        list.add(null); // Nulls are perfectly fine
        System.out.println("Initial list: " + list);
        System.out.println("Underlying Class: " + list.getClass().getName());

        // 2. Structural modification (Add/Remove)
        list.add("Hibernate");
        list.remove(0); // Removes "Java"
        System.out.println("After structural changes (add 'Hibernate', remove index 0): " + list);

        // 3. Value modification (Set)
        list.set(0, "Spring Boot"); // Replaces "Spring"
        System.out.println("After value modification (set index 0 to 'Spring Boot'): " + list);
        System.out.println("Result: Fully mutable and resizable.\n");
    }

    /**
     * Demonstrates Arrays.asList().
     * Key characteristics: Fixed-size, backed by original array, allows nulls, structural changes forbidden, content modifications allowed.
     */
    private static void demoArraysAsList() {
        printSubHeader("2. Array Wrapper List: Arrays.asList(...)");

        // 1. Creation from array
        String[] colors = {"Red", "Green", "Blue", null}; // Nulls are allowed
        List<String> list = Arrays.asList(colors);
        System.out.println("Initial wrapper list: " + list);
        System.out.println("Underlying Class: " + list.getClass().getName());

        // 2. Value modification (Allowed and writes through to array!)
        list.set(1, "Yellow");
        System.out.println("After setting index 1 to 'Yellow': " + list);
        System.out.println("Underlying array (colors) now: " + Arrays.toString(colors));
        System.out.println("Notice: Modifying the list modified the underlying array directly!");

        // Modifying underlying array writes through to the list as well
        colors[2] = "Purple";
        System.out.println("After modifying original array directly: colors[2] = 'Purple'");
        System.out.println("Wrapper list now: " + list);

        // 3. Structural modification (Forbidden!)
        try {
            System.out.print("Attempting to add elements: ");
            list.add("Orange");
        } catch (UnsupportedOperationException e) {
            System.out.println("FAILED with UnsupportedOperationException!");
            System.out.println("Explanation: Arrays.asList() is fixed-size. You cannot add elements.");
        }

        try {
            System.out.print("Attempting to remove elements: ");
            list.remove(0);
        } catch (UnsupportedOperationException e) {
            System.out.println("FAILED with UnsupportedOperationException!");
            System.out.println("Explanation: Arrays.asList() is fixed-size. You cannot remove elements.");
        }
        System.out.println();
    }

    /**
     * Demonstrates List.of() (Java 9+).
     * Key characteristics: Completely immutable (structural and value edits forbidden), null-hostile, optimized.
     */
    private static void demoListOf() {
        printSubHeader("3. Immutable List: List.of(...) (Java 9+)");

        // 1. Creation
        List<String> list = List.of("Apple", "Banana", "Cherry");
        System.out.println("Initial immutable list: " + list);
        System.out.println("Underlying Class: " + list.getClass().getName());

        // 2. Null Hostility (Nulls are NOT allowed!)
        try {
            System.out.print("Attempting to create List.of() with a null element: ");
            List.of("Apple", null, "Cherry");
        } catch (NullPointerException e) {
            System.out.println("FAILED with NullPointerException!");
            System.out.println("Explanation: List.of() is null-hostile and strictly forbids null elements.");
        }

        // 3. Structural modification (Forbidden!)
        try {
            System.out.print("Attempting to add to List.of(): ");
            list.add("Date");
        } catch (UnsupportedOperationException e) {
            System.out.println("FAILED with UnsupportedOperationException!");
        }

        // 4. Value modification (Forbidden!)
        try {
            System.out.print("Attempting to set element in List.of(): ");
            list.set(0, "Apricot");
        } catch (UnsupportedOperationException e) {
            System.out.println("FAILED with UnsupportedOperationException!");
            System.out.println("Explanation: List.of() is completely immutable. No add, remove, or set allowed.");
        }
        System.out.println();
    }

    /**
     * Demonstrates Collections.unmodifiableList().
     * Key characteristics: Read-only view, content is not copied, modifications to underlying source reflect in view.
     */
    private static void demoCollectionsUnmodifiableList() {
        printSubHeader("4. Unmodifiable View: Collections.unmodifiableList(List)");

        // 1. Creation
        List<String> mutableBaseList = new ArrayList<>();
        mutableBaseList.add("Dog");
        mutableBaseList.add("Cat");
        
        List<String> unmodifiableView = Collections.unmodifiableList(mutableBaseList);
        System.out.println("Base List: " + mutableBaseList);
        System.out.println("Unmodifiable View List: " + unmodifiableView);
        System.out.println("Underlying Class of view: " + unmodifiableView.getClass().getName());

        // 2. Direct modification attempts on the view (Forbidden!)
        try {
            System.out.print("Attempting to add directly to the unmodifiable view: ");
            unmodifiableView.add("Elephant");
        } catch (UnsupportedOperationException e) {
            System.out.println("FAILED with UnsupportedOperationException!");
            System.out.println("Explanation: You cannot modify the unmodifiableList wrapper directly.");
        }

        try {
            System.out.print("Attempting to set element directly in the unmodifiable view: ");
            unmodifiableView.set(0, "Puppy");
        } catch (UnsupportedOperationException e) {
            System.out.println("FAILED with UnsupportedOperationException!");
        }

        // 3. The Crucial Difference: Backing modifications (Reflected!)
        System.out.println("\nModifying the base backing list instead...");
        mutableBaseList.add("Elephant");
        mutableBaseList.set(1, "Kitten");
        System.out.println("Base List after modifications: " + mutableBaseList);
        System.out.println("Unmodifiable View List after backing list changed: " + unmodifiableView);
        System.out.println("WARNING: Unmodifiable view changed! It is not truly immutable because the backing source was modified.");
        System.out.println();
    }

    /**
     * Highlight comparison between true immutability (List.of) and read-only views (Collections.unmodifiableList).
     */
    private static void demoCrucialViewBehavior() {
        printSubHeader("Deep Dive: True Immutability (List.of) vs Read-Only View (Collections.unmodifiableList)");

        // Let's create an original list
        List<String> original = new ArrayList<>();
        original.add("Alpha");
        original.add("Beta");

        // Create both
        List<String> unmodifiableView = Collections.unmodifiableList(original);
        List<String> immutableCopy = List.copyOf(original); // List.copyOf is equivalent to List.of but copies a collection

        System.out.println("Original list: " + original);
        System.out.println("Collections.unmodifiableList (View): " + unmodifiableView);
        System.out.println("List.copyOf / List.of (Immutable Copy): " + immutableCopy);

        // Modify the original list
        System.out.println("\nModifying original list: original.add(\"Gamma\")");
        original.add("Gamma");

        System.out.println("Original list now: " + original);
        System.out.println("Collections.unmodifiableList (View) now: " + unmodifiableView + " (CHANGED!)");
        System.out.println("List.copyOf / List.of (Immutable Copy) now: " + immutableCopy + " (STABLE & SAFE!)");
        System.out.println();
    }

    private static void printHeader(String text) {
        String border = "=".repeat(75);
        System.out.println(border);
        System.out.println("   " + text);
        System.out.println(border);
    }

    private static void printSubHeader(String text) {
        System.out.println("--- " + text + " ---");
    }

    private static void printSummaryConclusion() {
        printHeader("RECOMMENDATION SUMMARY & CHEATSHEET");
        System.out.println("1. Use standard 'new ArrayList<>()' if you need a fully flexible, growing, shrinking list.");
        System.out.println("2. Use 'Arrays.asList()' if you want a list with a fixed size backed by an array, and changes");
        System.out.println("   should propagate between the list and array.");
        System.out.println("3. Use 'List.of()' or 'List.copyOf()' (Java 9+) when you want standard, secure, completely");
        System.out.println("   immutable lists that strictly prevent any modifications and fail early on null values.");
        System.out.println("4. Use 'Collections.unmodifiableList()' if you are exposing an internal mutable list to the");
        System.out.println("   outside world but want to protect it from direct external structural/content modifications,");
        System.out.println("   while still allowing internal updates to automatically propagate to the clients.");
        System.out.println("=".repeat(75));
    }
}

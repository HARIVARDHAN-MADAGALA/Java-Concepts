⚙️ **Factory Design Pattern in Java**
=====================================

🧩 1️⃣ What is the Factory Pattern?
-----------------------------------

> **Factory Pattern** is a **Creational Design Pattern** that provides an interface for creating objects,but allows subclasses or methods to decide which class to instantiate.

✅ **Simple meaning:**Instead of using new keyword directly everywhere,you use a **Factory class or method** to create objects for you.

💡 Real-World Analogy
---------------------

Imagine a **Car Factory** 🚗

*   You ask the factory: “Give me a car of type Sedan.”

*   You don’t build it yourself — the factory builds it and gives it to you.


So, your code doesn’t depend on **how** the car is created — just **what** you need.

⚙️ 2️⃣ Problem Without Factory Pattern
--------------------------------------

You directly create objects:

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   Shape shape = new Circle(); // Tight coupling ❌   `

Now, if you want a Rectangle, you must change code everywhere.

✅ 3️⃣ With Factory Pattern
--------------------------

You ask the factory to do it:

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   Shape shape = ShapeFactory.getShape("CIRCLE");   `

Now the creation logic is **centralized** and **flexible** —you can add more shapes without touching existing code. 🔥

🧠 4️⃣ Factory Pattern UML Concept
----------------------------------

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML             `+---------------------+               |      Shape          |  <-- Interface               +---------------------+               | + draw()            |               +---------------------+                        ▲        ┌───────────────┼─────────────────┐        │               │                 │  +-----------+   +-----------+    +-----------+  |  Circle   |   | Rectangle |    | Triangle  |  +-----------+   +-----------+    +-----------+  | + draw()  |   | + draw()  |    | + draw()  |  +-----------+   +-----------+    +-----------+                     ▲                     |          +------------------------+          |      ShapeFactory      |          +------------------------+          | + getShape(String)     |          +------------------------+`

🧱 5️⃣ Example Code — Step by Step
----------------------------------

### Step 1 — Create the Interface

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   public interface Shape {      void draw();  }   `

### Step 2 — Create Implementations

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   public class Circle implements Shape {      public void draw() {          System.out.println("Drawing Circle");      }  }  public class Rectangle implements Shape {      public void draw() {          System.out.println("Drawing Rectangle");      }  }  public class Square implements Shape {      public void draw() {          System.out.println("Drawing Square");      }  }   `

### Step 3 — Create Factory Class

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   public class ShapeFactory {      public static Shape getShape(String shapeType) {          if (shapeType == null) {              return null;          }          switch (shapeType.toUpperCase()) {              case "CIRCLE":                  return new Circle();              case "RECTANGLE":                  return new Rectangle();              case "SQUARE":                  return new Square();              default:                  throw new IllegalArgumentException("Unknown shape type: " + shapeType);          }      }  }   `

### Step 4 — Use Factory in Main

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   public class FactoryPatternDemo {      public static void main(String[] args) {          Shape shape1 = ShapeFactory.getShape("CIRCLE");          shape1.draw();          Shape shape2 = ShapeFactory.getShape("RECTANGLE");          shape2.draw();          Shape shape3 = ShapeFactory.getShape("SQUARE");          shape3.draw();      }  }   `

🧾 **Output:**

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   Drawing Circle  Drawing Rectangle  Drawing Square   `

🧠 6️⃣ Why Use Factory Pattern
------------------------------

ProblemFactory SolutionToo many new keywords everywhereCentralize object creationTight coupling between code & classesDecouple creation logicHard to change object types laterChange in one place onlyDifficult to maintainEasy to extend

⚡ 7️⃣ Types of Factory Patterns
-------------------------------

TypeDescription**Simple Factory**One static method decides which object to create (like we did).**Factory Method Pattern**Subclasses decide which class to instantiate. (Abstract Factory Pattern builds on this).**Abstract Factory Pattern**Factory of factories — creates families of related objects.

🧱 8️⃣ Example: Factory Method Pattern (Advanced)
-------------------------------------------------

When each subclass decides its own creation logic:

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   abstract class Plan {      protected double rate;      abstract void setRate();      public void calculateBill(int units) {          System.out.println(units * rate);      }  }  class DomesticPlan extends Plan {      void setRate() { rate = 3.50; }  }  class CommercialPlan extends Plan {      void setRate() { rate = 7.50; }  }  class PlanFactory {      public Plan getPlan(String type) {          if (type.equalsIgnoreCase("DOMESTIC")) return new DomesticPlan();          else if (type.equalsIgnoreCase("COMMERCIAL")) return new CommercialPlan();          return null;      }  }   `

🧾 9️⃣ Summary Table
--------------------

ConceptDescriptionCategoryCreational PatternPurposeCentralize object creation logicKey ClassesInterface, Implementations, FactoryAdvantagesLoose coupling, scalability, clean codeReal ExampleDriverManager.getConnection() in JDBC, BeanFactory in Spring

🧠 1️⃣0️⃣ Factory Pattern in **Spring Boot**
--------------------------------------------

Spring itself uses Factory patterns internally:

*   BeanFactory → Core factory for beans

*   ApplicationContext → Extended version of BeanFactory

*   @Bean or @Component annotated classes → Spring automatically acts as your factory


So in Spring, **you rarely write your own factories**,because **Spring IoC container** is already the “Factory” that creates and injects objects for you.

✅ **In one line (interview answer):**

> Factory Pattern is a creational pattern that lets you create objects without exposing the creation logic, by using a common interface and a centralized factory method.
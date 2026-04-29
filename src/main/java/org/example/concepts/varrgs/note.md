Perfect 👏 — this is a _very common Java interview topic_!Let’s go through **Variable Length Arguments** (also called **varargs**) step by step — clear, simple, and full explanation 👇

⚙️ **Variable Length Arguments in Java (varargs)**
==================================================

🧩 1️⃣ What It Means
--------------------

Variable-length arguments allow a method to **accept zero or more arguments of the same type**.

You don’t need to define multiple overloaded methods for different numbers of arguments.

### 💬 Real-life analogy

Imagine a method that adds numbers.Without varargs, you’d need many versions:

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   add(int a, int b)  add(int a, int b, int c)  add(int a, int b, int c, int d)   `

With varargs:

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   add(int... numbers)   `

Now it can take **any number** of int values — even 0!

⚙️ 2️⃣ Syntax
-------------

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   returnType methodName(dataType... variableName)   `

Example:

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   public static void addNumbers(int... nums)   `

*   The ... means “zero or more arguments”.

*   Inside the method, nums behaves like an **array**.


✅ 3️⃣ Example
-------------

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   public class VarArgsExample {      static void show(int... numbers) {          System.out.println("Count: " + numbers.length);          for (int n : numbers) {              System.out.print(n + " ");          }          System.out.println();      }      public static void main(String[] args) {          show(10);              // one argument          show(1, 2, 3, 4, 5);   // multiple arguments          show();                // zero arguments      }  }   `

🧾 **Output:**

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   Count: 1  10  Count: 5  1 2 3 4 5  Count: 0   `

🧠 4️⃣ Internal Working
-----------------------

At compile time, Java converts the varargs parameter into an **array** automatically.

So:

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   show(1, 2, 3);   `

is compiled as:

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   show(new int[]{1, 2, 3});   `

That’s why inside the method you can treat numbers as an array.

⚙️ 5️⃣ Rules for Using Varargs
------------------------------

RuleDescription✅ Only one varargs parameter allowedA method can have only **one** varargs argument.✅ Must be last parameterVarargs must come at the **end** of the parameter list.✅ Can pass zero or more argumentsIt’s optional to pass values.

### ❌ Invalid Example

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   void test(int... nums, String name) { }  // ❌ varargs must be last   `

### ✅ Correct Example

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   void test(String name, int... nums) { }  // ✅ last position   `

🧩 6️⃣ Example with Normal + Varargs Together
---------------------------------------------

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   public static void greet(String message, String... names) {      for (String n : names) {          System.out.println(message + " " + n);      }  }  public static void main(String[] args) {      greet("Hello", "Hari", "Vardhan", "Red");  }   `

🧾 **Output:**

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   Hello Hari  Hello Vardhan  Hello Red   `

🧠 7️⃣ Difference Between Varargs and Array
-------------------------------------------

FeatureVarargsArrayDeclarationint... numsint\[\] numsWhen to useWhen caller passes variable countWhen fixed-size knownCaller Syntaxshow(1, 2, 3)show(new int\[\]{1,2,3})Flexibility✅ High❌ Limited

Both are same internally — varargs is just **syntactic sugar**.

💡 8️⃣ Common Interview Trick Question
--------------------------------------

**Can we overload methods using varargs?**✅ Yes — but be careful, it can cause ambiguity.

Example:

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   void show(int a, int b) { }  void show(int... a) { }  // ✅ valid   `

Calling show(10, 20) will call the **non-varargs version**, since it’s a more specific match.

🧾 9️⃣ Summary
--------------

ConceptDescriptionKeyword... (three dots)PurposePass variable number of argumentsInternalTreated as arrayPositionMust be last parameterLimitOnly one per method

✅ **In One Line (Interview Answer):**

> Variable-length arguments (varargs) allow a method to accept any number of arguments of the same type.It’s declared using ..., treated as an array internally, and must be the last parameter in the method.
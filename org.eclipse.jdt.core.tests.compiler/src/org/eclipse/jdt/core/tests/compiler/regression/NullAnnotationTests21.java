/*******************************************************************************
 * Copyright (c) 2021, 2023 GK Software SE, and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephan Herrmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.compiler.regression;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.tests.util.Util;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

import junit.framework.Test;

public class NullAnnotationTests21 extends AbstractNullAnnotationTest {

	public NullAnnotationTests21(String name) {
		super(name);
	}

	static {
//			TESTS_NAMES = new String[] { "test_totalTypePatternNonNullExpression" };
//			TESTS_NUMBERS = new int[] { 001 };
//			TESTS_RANGE = new int[] { 1, 12 };
	}

	public static Test suite() {
		return buildMinimalComplianceTestSuite(testClass(), F_21);
	}

	public static Class<?> testClass() {
		return NullAnnotationTests21.class;
	}

	@Deprecated // super method is deprecated
	@Override
	protected void setUpAnnotationLib() throws IOException {
		if (this.LIBS == null) {
			String[] defaultLibs = getDefaultClassPaths();
			int len = defaultLibs.length;
			this.LIBS = new String[len+1];
			System.arraycopy(defaultLibs, 0, this.LIBS, 0, len);
			this.LIBS[len] = NullAnnotationTests9.createAnnotation_2_2_jar(Util.getOutputDirectory() + File.separator, null);
		}
	}

	// -------- helper ------------

	private Runner getDefaultRunner() {
		Runner runner = new Runner();
		runner.classLibraries = this.LIBS;
		Map<String,String> opts = getCompilerOptions();
		opts.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_21);
		runner.customOptions = opts;
		runner.javacTestOptions =
				JavacTestOptions.Excuse.EclipseWarningConfiguredAsError;
		return runner;
	}

	// --------- tests start -----------

	public void test_typePatternIsNN() {
		Runner runner = getDefaultRunner();
		runner.testFiles = new String[] {
				"X.java",
				  "import org.eclipse.jdt.annotation.*;\n" +
				  "public class X {\n" +
				  "	void foo(Object o) {\n" +
				  "		switch (o) {\n" +
				  "			case Integer i -> consumeInt(i);\n" +
				  "			default -> System.out.println(\"default\");\n" +
				  "		}\n" +
				  "	}\n" +
				  "	void consumeInt(@NonNull Integer i) {\n" +
				  "		System.out.print(i);\n" +
				  "	}\n" +
				  "	public static void main(String... args) {\n" +
				  "		new X().foo(3);\n" +
				  "	}\n" +
				  "}\n"
			};
		runner.expectedCompilerLog = "";
		runner.expectedOutputString = "3";
		runner.runConformTest();
	}

	public void test_totalTypePatternDoesNotAdmitNull() {
		Runner runner = getDefaultRunner();
		runner.testFiles = new String[] {
				"X.java",
				  "import org.eclipse.jdt.annotation.*;\n" +
				  "public class X {\n" +
				  "	void foo(Number n) {\n" +
				  "		try {\n" +
				  "			switch (n) {\n" +
				  "				case Integer i -> consumeInt(i);\n" +
				  "				case Number n0 -> consumeNumber(n0);\n" +
				  "			}\n" +
				  "		} catch (NullPointerException npe) {\n" +
				  "			// ignoring the unchecked warning, and expecting the NPE:\n" +
				  "			System.out.print(npe.getMessage());\n" +
				  "		}\n" +
				  "	}\n" +
				  "	void consumeInt(@NonNull Integer i) {\n" +
				  "		System.out.print(i);\n" +
				  "	}\n" +
				  "	void consumeNumber(@NonNull Number n) {\n" +
				  "		System.out.print(n.toString());\n" +
				  "	}\n" +
				  "	public static void main(String... args) {\n" +
				  "		new X().foo(null);\n" +
				  "	}\n" +
				  "}\n"
			};
		runner.expectedCompilerLog =
				"----------\n" +
				"1. WARNING in X.java (at line 7)\n" +
				"	case Number n0 -> consumeNumber(n0);\n" +
				"	                                ^^\n" +
				"Null type safety (type annotations): The expression of type \'Number\' needs unchecked conversion to conform to \'@NonNull Number\'\n" +
				"----------\n";
//		runner.expectedOutputString = "Cannot invoke \"Object.toString()\" because \"n\" is null";
		runner.expectedOutputString = "null";
		runner.runConformTest();
	}

	public void test_totalTypePatternNonNullExpression() {
		Runner runner = getDefaultRunner();
		runner.testFiles = new String[] {
				"X.java",
				  "import org.eclipse.jdt.annotation.*;\n" +
				  "public class X {\n" +
				  "	void foo(Number n) {\n" +
				  "		if (n == null) return;\n" + // this prevents the NPE -> no need to warn
				  "		switch (n) {\n" +
				  "			case Integer i -> System.out.print(i);\n" +
				  "			case Number n0 -> consumeNumber(n0);\n" +
				  "		}\n" +
				  "	}\n" +
				  "	void consumeNumber(@NonNull Number n) {\n" +
				  "		System.out.print(n.toString());\n" +
				  "	}\n" +
				  "	public static void main(String... args) {\n" +
				  "		new X().foo(null);\n" +
				  "	}\n" +
				  "}\n"
			};
		runner.expectedCompilerLog = "";
		runner.expectedOutputString = "";
		runner.runConformTest();
	}

	public void test_totalTypePatternNonNullExpression_swExpr() {
		Runner runner = getDefaultRunner();
		runner.testFiles = new String[] {
				"X.java",
				  "import org.eclipse.jdt.annotation.*;\n" +
				  "public class X {\n" +
				  "	int foo(Number n) {\n" +
				  "		if (n == null) return -1;\n" + // this prevents the NPE -> no need to warn
				  "		return switch (n) {\n" +
				  "			case Integer i -> i;\n" +
				  "			case Number n0 -> consumeNumber(n0);\n" +
				  "		};\n" +
				  "	}\n" +
				  "	int consumeNumber(@NonNull Number n) {\n" +
				  "		return Integer.valueOf(n.toString());\n" +
				  "	}\n" +
				  "	public static void main(String... args) {\n" +
				  "		new X().foo(null);\n" +
				  "	}\n" +
				  "}\n"
			};
		runner.expectedCompilerLog = "";
		runner.expectedOutputString = "";
		runner.runConformTest();
	}

	public void test_totalTypePatternPlusNullPattern() {
		Runner runner = getDefaultRunner();
		runner.testFiles = new String[] {
				"X.java",
				  "import org.eclipse.jdt.annotation.*;\n" +
				  "public class X {\n" +
				  "	void foo(Number n) {\n" +
				  "		switch (n) {\n" +
				  "			case null -> System.out.print(\"null\");\n" + // this prevents the NPE
				  "			case Integer i -> System.out.print(i);\n" +
				  "			case Number n0 -> consumeNumber(n0);\n" +
				  "		}\n" +
				  "	}\n" +
				  "	void consumeNumber(@NonNull Number n) {\n" +
				  "		System.out.print(n.toString());\n" +
				  "	}\n" +
				  "	public static void main(String... args) {\n" +
				  "		new X().foo(null);\n" +
				  "	}\n" +
				  "}\n"
			};
		runner.expectedCompilerLog = "";
		runner.expectedOutputString = "null";
		runner.runConformTest();
	}

	public void test_totalTypePatternNullableExpression() {
		Runner runner = getDefaultRunner();
		runner.testFiles = new String[] {
				"X.java",
				  "import org.eclipse.jdt.annotation.*;\n" +
				  "public class X {\n" +
				  "	void foo(@Nullable Number n) {\n" + // @Nullable here turns "unchecked" into "null type mismatch"
				  "		switch (n) {\n" +
				  "			case Integer i -> System.out.print(i);\n" +
				  "			case Number n0 -> consumeNumber(n0);\n" +
				  "		}\n" +
				  "	}\n" +
				  "	void consumeNumber(@NonNull Number n) {\n" +
				  "		System.out.print(n.toString());\n" +
				  "	}\n" +
				  "	public static void main(String... args) {\n" +
				  "		new X().foo(null);\n" +
				  "	}\n" +
				  "}\n"
			};
		runner.expectedCompilerLog =
				"----------\n" +
				"1. ERROR in X.java (at line 6)\n" +
				"	case Number n0 -> consumeNumber(n0);\n" +
				"	                                ^^\n" +
				"Null type mismatch: required \'@NonNull Number\' but the provided value is inferred as @Nullable\n" +
				"----------\n";
		runner.runNegativeTest();
	}

	public void test_switchOverNNValueWithNullCase() {
		Runner runner = getDefaultRunner();
		runner.customOptions.put(CompilerOptions.OPTION_ReportRedundantNullCheck, CompilerOptions.WARNING);
		runner.testFiles = new String[] {
				"X.java",
				  "import org.eclipse.jdt.annotation.*;\n" +
				  "public class X {\n" +
				  "	void foo(@NonNull Object o) {\n" +
				  "		switch (o) {\n" +
				  "			case Integer i -> consumeInt(i);\n" +
				  "			case null -> System.out.print(\"null\");\n" +
				  "			default -> System.out.println(\"default\");\n" +
				  "		}\n" +
				  "	}\n" +
				  "	void consumeInt(@NonNull Integer i) {\n" +
				  "		System.out.print(i);\n" +
				  "	}\n" +
				  "	public static void main(String... args) {\n" +
				  "		new X().foo(3);\n" +
				  "	}\n" +
				  "}\n"
			};
		runner.expectedCompilerLog =
				"----------\n" +
				"1. WARNING in X.java (at line 6)\n" +
				"	case null -> System.out.print(\"null\");\n" +
				"	^^^^^^^^^\n" +
				"Unnecessary \'null\' pattern, the switch selector expression cannot be null\n" +
				"----------\n";
		runner.expectedOutputString = "3";
		runner.runConformTest();
	}

	// null cannot be in the same case with pattern as per the 432+433 jep
	public void _test_switchNullInSameCase() {
		Runner runner = getDefaultRunner();
		runner.customOptions.put(CompilerOptions.OPTION_ReportRedundantNullCheck, CompilerOptions.WARNING);
		runner.testFiles = new String[] {
				"X.java",
				  "import org.eclipse.jdt.annotation.*;\n" +
				  "public class X {\n" +
				  "	void foo(Object o) {\n" +
				  "		switch (o) {\n" +
				  "			case null, Integer i -> consumeInt(i);\n" +
				  "			default -> System.out.println(\"default\");\n" +
				  "		}\n" +
				  "	}\n" +
				  "	void consumeInt(@NonNull Integer i) {\n" +
				  "		System.out.print(i);\n" +
				  "	}\n" +
				  "	public static void main(String... args) {\n" +
				  "		new X().foo(3);\n" +
				  "	}\n" +
				  "}\n"
			};
		runner.expectedCompilerLog =
				"----------\n" +
				"1. ERROR in X.java (at line 5)\n" +
				"	case null, Integer i -> consumeInt(i);\n" +
				"	                                   ^\n" +
				"Null type mismatch: required \'@NonNull Integer\' but the provided value is inferred as @Nullable\n" +
				"----------\n";
		runner.runNegativeTest();
	}

	public void test_switchOverNNValueWithNullCase_swExpr() {
		Runner runner = getDefaultRunner();
		runner.customOptions.put(CompilerOptions.OPTION_ReportRedundantNullCheck, CompilerOptions.WARNING);
		runner.testFiles = new String[] {
				"X.java",
				  "import org.eclipse.jdt.annotation.*;\n" +
				  "public class X {\n" +
				  "	int foo(@NonNull Object o) {\n" +
				  "		return switch (o) {\n" +
				  "			case Integer i -> consumeInt(i);\n" +
				  "			case null -> 0;\n" +
				  "			default -> -1;\n" +
				  "		};\n" +
				  "	}\n" +
				  "	int consumeInt(@NonNull Integer i) {\n" +
				  "		return i;\n" +
				  "	}\n" +
				  "	public static void main(String... args) {\n" +
				  "		System.out.print(new X().foo(3));\n" +
				  "	}\n" +
				  "}\n"
			};
		runner.expectedCompilerLog =
				"----------\n" +
				"1. WARNING in X.java (at line 6)\n" +
				"	case null -> 0;\n" +
				"	^^^^^^^^^\n" +
				"Unnecessary \'null\' pattern, the switch selector expression cannot be null\n" +
				"----------\n";
		runner.expectedOutputString = "3";
		runner.runConformTest();
	}

	public void test_nullHostileSwitch() {
		Runner runner = getDefaultRunner();
		runner.customOptions.put(CompilerOptions.OPTION_ReportRedundantNullCheck, CompilerOptions.WARNING);
		runner.testFiles = new String[] {
				"X.java",
				  "import org.eclipse.jdt.annotation.*;\n" +
				  "public class X {\n" +
				  "	void foo(@Nullable Object o) {\n" +
				  "		switch (o) {\n" +
				  "			case Integer i -> consumeInt(i);\n" +
				  "			default -> System.out.println(o);\n" +
				  "		};\n" +
				  "	}\n" +
				  "	void consumeInt(@NonNull Integer i) {\n" +
				  "	}\n" +
				  "	public static void main(String... args) {\n" +
				  "		new X().foo(null);\n" +
				  "	}\n" +
				  "}\n"
			};
		runner.expectedCompilerLog =
				"----------\n" +
				"1. ERROR in X.java (at line 4)\n" +
				"	switch (o) {\n" +
				"	        ^\n" +
				"Potential null pointer access: this expression has a \'@Nullable\' type\n" +
				"----------\n";
		runner.runNegativeTest();
	}

	public void test_defaultDoesNotApplyToNull() {
		Runner runner = getDefaultRunner();
		runner.customOptions.put(CompilerOptions.OPTION_ReportRedundantNullCheck, CompilerOptions.WARNING);
		runner.testFiles = new String[] {
				"X.java",
				  "import org.eclipse.jdt.annotation.*;\n" +
				  "public class X {\n" +
				  "	void foo(@Nullable Object o) {\n" +
				  "		switch (o) {\n" +
				  "			case Integer i -> consumeInt(i);\n" +
				  "			case null -> System.out.print(\"null\");\n" +
				  "			default -> System.out.println(o.toString());\n" +
				  "		};\n" +
				  "	}\n" +
				  "	void consumeInt(@NonNull Integer i) {\n" +
				  "	}\n" +
				  "	public static void main(String... args) {\n" +
				  "		new X().foo(null);\n" +
				  "	}\n" +
				  "}\n"
			};
		runner.expectedCompilerLog = "";
		runner.expectedOutputString = "null";
		runner.runConformTest();
	}

	public void test_defaultDoesNotApplyToNull_field() {
		Runner runner = getDefaultRunner();
		runner.customOptions.put(CompilerOptions.OPTION_SyntacticNullAnalysisForFields, CompilerOptions.ENABLED);
		runner.testFiles = new String[] {
				"X.java",
				  "import org.eclipse.jdt.annotation.*;\n" +
				  "public class X {\n" +
				  "	@Nullable Object o;\n" +
				  "	void foo() {\n" +
				  "		switch (this.o) {\n" +
				  "			case Integer i -> consumeInt(i);\n" +
				  "			case null -> System.out.print(\"null\");\n" +
				  "			default -> System.out.println(this.o.toString());\n" +
				  "		};\n" +
				  "	}\n" +
				  "	void consumeInt(@NonNull Integer i) {\n" +
				  "	}\n" +
				  "	public static void main(String... args) {\n" +
				  "		new X().foo();\n" +
				  "	}\n" +
				  "}\n"
			};
		runner.expectedCompilerLog = "";
		runner.expectedOutputString = "null";
		runner.runConformTest();
	}

	public void test_defaultDoesNotApplyToNull_field2() {
		Runner runner = getDefaultRunner();
		runner.customOptions.put(CompilerOptions.OPTION_SyntacticNullAnalysisForFields, CompilerOptions.ENABLED);
		runner.testFiles = new String[] {
				"X.java",
				  "import org.eclipse.jdt.annotation.*;\n" +
				  "public class X {\n" +
				  "	@Nullable Object o;\n" +
				  "	void foo(X x) {\n" +
				  "		switch (x.o) {\n" +
				  "			case Integer i -> consumeInt(i);\n" +
				  "			case null -> System.out.print(\"null\");\n" +
				  "			default -> System.out.println(x.o.toString());\n" +
				  "		};\n" +
				  "	}\n" +
				  "	void consumeInt(@NonNull Integer i) {\n" +
				  "	}\n" +
				  "	public static void main(String... args) {\n" +
				  "		new X().foo(new X());\n" +
				  "	}\n" +
				  "}\n"
			};
		runner.expectedCompilerLog = "";
		runner.expectedOutputString = "null";
		runner.runConformTest();
	}

	public void testBug576329() {
		Runner runner = getDefaultRunner();
		runner.customOptions.put(CompilerOptions.OPTION_SyntacticNullAnalysisForFields, CompilerOptions.ENABLED);
		runner.testFiles = new String[] {
				"Main.java",
				"public class Main {\n" +
				"    int length;\n" +
				"    public String switchOnArray(Object argv[]) {\n" +
				"        return switch(argv.length) {\n" +
				"        case 0 -> \"0\";\n" +
				"        default -> \"x\";\n" +
				"        };\n" +
				"    }\n" +
				"	public static void main(String... args) {\n" +
				"		System.out.print(new Main().switchOnArray(args));\n" +
				"	}\n" +
				"}\n"
			};
		runner.expectedCompilerLog = "";
		runner.expectedOutputString = "0";
		runner.runConformTest();
	}

	public void testInstanceOfPatternIsNonNull() {
		Runner runner = getDefaultRunner();
		runner.testFiles = new String[] {
				"X.java",
				"import org.eclipse.jdt.annotation.*;\n" +
				"public class X {\n" +
				"	public static void consumeNonNull(@NonNull String s) {\n" +
				"		System.out.println(\"nonnull\");\n" +
				"	}\n" +
				"	public static void main(String... args) {\n" +
				"		Object o = Math.random() < 0 ? new Object() : \"blah\";\n" +
				"		if (o instanceof String message) {\n" +
				"			consumeNonNull(message);\n" +
				"		}\n" +
				"	}\n" +
				"}\n"
			};
		runner.expectedCompilerLog = "";
		runner.expectedOutputString = "nonnull";
		runner.runConformTest();
	}

	public void testInstanceOfPatternIsLaterAssignedNull() {
		Runner runner = getDefaultRunner();
		runner.testFiles = new String[] {
				"X.java",
				"import org.eclipse.jdt.annotation.*;\n" +
				"public class X {\n" +
				"	public static void consumeNonNull(@NonNull String s) {\n" +
				"		System.out.println(\"nonnull\");\n" +
				"	}\n" +
				"	public static void main(String... args) {\n" +
				"		Object o = Math.random() >= 0 ? new Object() : \"blah\";\n" +
				"		if (o instanceof String message) {\n" +
				"			consumeNonNull(message);\n" +
				"			message = null;\n" +
				"			consumeNonNull(message);\n" +
				"		}\n" +
				"	}\n" +
				"}\n"
			};
		runner.expectedCompilerLog =
				"----------\n" +
				"1. ERROR in X.java (at line 11)\n" +
				"	consumeNonNull(message);\n" +
				"	               ^^^^^^^\n" +
				"Null type mismatch: required \'@NonNull String\' but the provided value is null\n" +
				"----------\n";
		runner.runNegativeTest();
	}

	// since 11: uses 'var'
	public void testNullableVar() {
		Runner runner = getDefaultRunner();
		runner.testFiles = new String[] {
				"Test.java",
				"\n" +
				"import org.eclipse.jdt.annotation.NonNull;\n" +
				"\n" +
				"public class Test {\n" +
				"	public @NonNull Test getSomeValue() { return this; }\n" +
				"	\n" +
				"	void test(boolean rainyDay) {\n" +
				"		var a = rainyDay ? getSomeValue() : null;\n" +
				"		a.getSomeValue(); // problem not detected\n" +
				"	}\n" +
				"	void test2(boolean rainyDay) {\n" +
				"		Test a = rainyDay ? getSomeValue() : null;\n" +
				"		a.getSomeValue(); // Potential null pointer access: The variable a may be null at this location\n" +
				"	}\n" +
				"}\n"
			};
		runner.expectedCompilerLog =
				"----------\n" +
				"1. ERROR in Test.java (at line 9)\n" +
				"	a.getSomeValue(); // problem not detected\n" +
				"	^\n" +
				"Potential null pointer access: The variable a may be null at this location\n" +
				"----------\n" +
				"2. ERROR in Test.java (at line 13)\n" +
				"	a.getSomeValue(); // Potential null pointer access: The variable a may be null at this location\n" +
				"	^\n" +
				"Potential null pointer access: The variable a may be null at this location\n" +
				"----------\n";
		runner.runNegativeTest();
	}
	public void _testGH629_01() {
		Map<String, String> options = getCompilerOptions();
		options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_18);
		options.put(JavaCore.COMPILER_NONNULL_ANNOTATION_NAME, "test.NonNull");
		options.put(JavaCore.COMPILER_NULLABLE_ANNOTATION_NAME, "test.Nullable");
		options.put(JavaCore.COMPILER_ANNOTATION_NULL_ANALYSIS, JavaCore.ENABLED);

		runNegativeTestWithLibs(
				new String[] {
						"Configuration.java",
						"public interface Configuration {\n" +
								"}\n",
						"Init.java",
						"public interface Init<C extends Configuration> {\n" +
								"}\n",
						"Annot.java",
						"public @interface Annot {\n" +
								"    Class<? extends Init<? extends Configuration>>[] inits(); \n" +
								"}\n",
						"App.java",
						"interface I<T> {}\n" +
						"@Annot(inits = {App.MyInit.class})\n" +
								"public class App {\n" +
								"    static class MyInit implements I<String>, Init<Configuration> {}\n" +
								"}\n"
				},
				options,
				"");
	}
	public void _testGH629_02() {
		Map<String, String> options = getCompilerOptions();
		options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_18);
		options.put(JavaCore.COMPILER_NONNULL_ANNOTATION_NAME, "test.NonNull");
		options.put(JavaCore.COMPILER_NULLABLE_ANNOTATION_NAME, "test.Nullable");
		options.put(JavaCore.COMPILER_ANNOTATION_NULL_ANALYSIS, JavaCore.ENABLED);

		runNegativeTestWithLibs(
				new String[] {
						"Annot.java",
						"public @interface Annot {\n" +
								"    Class<? extends Init<? extends Configuration>>[] inits(); \n" +
								"}\n",
						"App.java",
						"@Annot(inits = {App.MyInit.class})\n" +
						"public class App {\n" +
						"    static class MyInit implements Init<Configuration> {}\n" +
						"}\n",
						"Configuration.java",
						"public interface Configuration {\n" +
								"}\n",
						"Init.java",
						"public interface Init<C extends Configuration> {\n" +
								"}\n"
				},
				options,
				"");
	}
	public void testBug572361() {
		runConformTestWithLibs(
			new String[] {
				"NonNullByDefaultAndRecords.java",
				"import org.eclipse.jdt.annotation.NonNullByDefault;\n" +
				"\n" +
				"@NonNullByDefault\n" +
				"public record NonNullByDefaultAndRecords () { }\n"
			},
			getCompilerOptions(),
			"");
	}

	public void testIssue233_ok() throws Exception {
		Runner runner = getDefaultRunner();
		runner.customOptions = getCompilerOptions();
		runner.customOptions.put(JavaCore.COMPILER_PB_REDUNDANT_NULL_ANNOTATION, JavaCore.IGNORE);
		runner.testFiles = new String[] {
				"X.java",
				"import org.eclipse.jdt.annotation.*;\n" +
				"record A1(@NonNull String ca1, String ca2) {}\n" +
				"record B1(@Nullable String cb1, String cb2) {}\n" +
				"@NonNullByDefault\n" +
				"public class X {\n" +
				"	record A2(@NonNull String ca1, String ca2) {}\n" +
				"	record B2(@Nullable String cb1, String cb2) {}\n" +
				"\n" +
				"	public static @NonNull String workWithA(A1 a, boolean f) {\n" +
				"		return f ? a.ca1() : a.ca2();\n" +
				"	}\n" +
				"	public static @NonNull String workWithA(A2 a, boolean f) {\n" +
				"		return f ? a.ca1() : a.ca2();\n" +
				"	}\n" +
				"	public static String workWithB(B1 b, boolean f) {\n" +
				"		if (f) {\n" +
				"			String c = b.cb1();\n" +
				"			return c != null ? c : \"default \";\n" +
				"		}\n" +
				"		return b.cb2();\n" +
				"	}\n" +
				"	public static String workWithB(B2 b, boolean f) {\n" +
				"		if (f) {\n" +
				"			String c = b.cb1();\n" +
				"			return c != null ? c : \"default \";\n" +
				"		}\n" +
				"		return b.cb2();\n" +
				"	}\n" +
				"	public static void main(String... args) {\n" +
				"		@NonNull String sa11 = workWithA(new A1(\"hello \", \"A11 \"), true);\n" +
				"		@NonNull String sa12 = workWithA(new A1(\"hello \", \"A12 \"), false);\n" +
				"		@NonNull String sb11 = workWithB(new B1(null, \"B11 \"), true);\n" +
				"		@NonNull String sb12 = workWithB(new B1(null, \"B12 \"), false);\n" +
				"		@NonNull String sa21 = workWithA(new A2(\"hello \", \"A21 \"), true);\n" +
				"		@NonNull String sa22 = workWithA(new A2(\"hello \", \"A22 \"), false);\n" +
				"		@NonNull String sb21 = workWithB(new B2(null, \"B21\"), true);\n" +
				"		@NonNull String sb22 = workWithB(new B2(null, \"B22\"), false);\n" +
				"		System.out.println(sa11+sa12+sb11+sb12+sa21+sa22+sb21+sb22);\n" +
				"	}\n" +
				"}\n"
			};
		runner.expectedOutputString = "hello A12 default B12 hello A22 default B22";
		runner.runConformTest();
	}
	public void testIssue233_nok() throws Exception {
		// like testIssue233_ok - but annotations on record components ca1 / cb1 swapped (twice)
		Runner runner = getDefaultRunner();
		runner.customOptions = getCompilerOptions();
		runner.customOptions.put(JavaCore.COMPILER_PB_REDUNDANT_NULL_ANNOTATION, JavaCore.IGNORE);
		runner.testFiles = new String[] {
				"X.java",
				"import org.eclipse.jdt.annotation.*;\n" +
				"record A1(@Nullable String ca1, String ca2) {}\n" +
				"record B1(@NonNull String cb1, String cb2) {}\n" +
				"@NonNullByDefault\n" +
				"public class X {\n" +
				"	record A2(@Nullable String ca1, String ca2) {}\n" +
				"	record B2(@NonNull String cb1, String cb2) {}\n" +
				"\n" +
				"	public static @NonNull String workWithA(A1 a, boolean f) {\n" +
				"		return f ? a.ca1() : a.ca2();\n" +
				"	}\n" +
				"	public static @NonNull String workWithA(A2 a, boolean f) {\n" +
				"		return f ? a.ca1() : a.ca2();\n" +
				"	}\n" +
				"	public static String workWithB(B1 b, boolean f) {\n" +
				"		if (f) {\n" +
				"			String c = b.cb1();\n" +
				"			return c != null ? c : \"default \";\n" +
				"		}\n" +
				"		return b.cb2();\n" +
				"	}\n" +
				"	public static String workWithB(B2 b, boolean f) {\n" +
				"		if (f) {\n" +
				"			String c = b.cb1();\n" +
				"			return c != null ? c : \"default \";\n" +
				"		}\n" +
				"		return b.cb2();\n" +
				"	}\n" +
				"	public static void main(String... args) {\n" +
				"		@NonNull String sa11 = workWithA(new A1(\"hello \", \"A11 \"), true);\n" +
				"		@NonNull String sa12 = workWithA(new A1(\"hello \", \"A12 \"), false);\n" +
				"		@NonNull String sb11 = workWithB(new B1(null, \"B11 \"), true);\n" +
				"		@NonNull String sb12 = workWithB(new B1(null, \"B12 \"), false);\n" +
				"		@NonNull String sa21 = workWithA(new A2(\"hello \", \"A21 \"), true);\n" +
				"		@NonNull String sa22 = workWithA(new A2(\"hello \", \"A22 \"), false);\n" +
				"		@NonNull String sb21 = workWithB(new B2(null, \"B21\"), true);\n" +
				"		@NonNull String sb22 = workWithB(new B2(null, \"B22\"), false);\n" +
				"		System.out.println(sa11+sa12+sb11+sb12+sa21+sa22+sb21+sb22);\n" +
				"	}\n" +
				"}\n"
			};
		runner.expectedCompilerLog =
				"----------\n" +
				"1. ERROR in X.java (at line 10)\n" +
				"	return f ? a.ca1() : a.ca2();\n" +
				"	           ^^^^^^^\n" +
				"Null type mismatch (type annotations): required \'@NonNull String\' but this expression has type \'@Nullable String\'\n" +
				"----------\n" +
				"2. WARNING in X.java (at line 10)\n" +
				"	return f ? a.ca1() : a.ca2();\n" +
				"	                     ^^^^^^^\n" +
				"Null type safety (type annotations): The expression of type \'String\' needs unchecked conversion to conform to \'@NonNull String\'\n" +
				"----------\n" +
				"3. ERROR in X.java (at line 13)\n" +
				"	return f ? a.ca1() : a.ca2();\n" +
				"	           ^^^^^^^\n" +
				"Null type mismatch (type annotations): required \'@NonNull String\' but this expression has type \'@Nullable String\'\n" +
				"----------\n" +
				"4. ERROR in X.java (at line 18)\n" +
				"	return c != null ? c : \"default \";\n" +
				"	       ^\n" +
				"Redundant null check: The variable c cannot be null at this location\n" +
				"----------\n" +
				"5. WARNING in X.java (at line 20)\n" +
				"	return b.cb2();\n" +
				"	       ^^^^^^^\n" +
				"Null type safety (type annotations): The expression of type \'String\' needs unchecked conversion to conform to \'@NonNull String\'\n" +
				"----------\n" +
				"6. ERROR in X.java (at line 25)\n" +
				"	return c != null ? c : \"default \";\n" +
				"	       ^\n" +
				"Redundant null check: The variable c cannot be null at this location\n" +
				"----------\n" +
				"7. ERROR in X.java (at line 32)\n" +
				"	@NonNull String sb11 = workWithB(new B1(null, \"B11 \"), true);\n" +
				"	                                        ^^^^\n" +
				"Null type mismatch: required \'@NonNull String\' but the provided value is null\n" +
				"----------\n" +
				"8. ERROR in X.java (at line 33)\n" +
				"	@NonNull String sb12 = workWithB(new B1(null, \"B12 \"), false);\n" +
				"	                                        ^^^^\n" +
				"Null type mismatch: required \'@NonNull String\' but the provided value is null\n" +
				"----------\n" +
				"9. ERROR in X.java (at line 36)\n" +
				"	@NonNull String sb21 = workWithB(new B2(null, \"B21\"), true);\n" +
				"	                                        ^^^^\n" +
				"Null type mismatch: required \'@NonNull String\' but the provided value is null\n" +
				"----------\n" +
				"10. ERROR in X.java (at line 37)\n" +
				"	@NonNull String sb22 = workWithB(new B2(null, \"B22\"), false);\n" +
				"	                                        ^^^^\n" +
				"Null type mismatch: required \'@NonNull String\' but the provided value is null\n" +
				"----------\n";
		runner.expectedOutputString = "hellodefault";
		runner.runNegativeTest();
	}
	public void testIssue233_npeWitness() throws Exception {
		Runner runner = getDefaultRunner();
		runner.testFiles = new String[] {
				"X.java",
				"import org.eclipse.jdt.annotation.*;\n" +
				"public record X(@NonNull String ca1, String ca2, @Nullable String ca2) {}\n"
			};
		runner.expectedCompilerLog =
				"----------\n" +
				"1. ERROR in X.java (at line 2)\n" +
				"	public record X(@NonNull String ca1, String ca2, @Nullable String ca2) {}\n" +
				"	                                            ^^^\n" +
				"Duplicate component ca2 in record\n" +
				"----------\n" +
				"2. ERROR in X.java (at line 2)\n" +
				"	public record X(@NonNull String ca1, String ca2, @Nullable String ca2) {}\n" +
				"	                                                                  ^^^\n" +
				"Duplicate component ca2 in record\n" +
				"----------\n" +
				"3. ERROR in X.java (at line 2)\n" +
				"	public record X(@NonNull String ca1, String ca2, @Nullable String ca2) {}\n" +
				"	                                                                  ^^^\n" +
				"Duplicate parameter ca2\n" +
				"----------\n";
		runner.runNegativeTest();
	}

	public void testGH1399() {
		Runner runner = new Runner();
		runner.testFiles = new String[] {
				"C.java",
				"""
				@interface Ann { Class<? extends A> value(); }
				class A {}
				@Ann(C.B.class) // <- ERROR: Type mismatch: cannot convert from Class<C.B> to Class<? extends A>
				class C<T extends Number> {
				    class B extends A {}
				}
				"""};
		runner.runConformTest();
	}

	public void testGH1399_2() {
		Runner runner = new Runner();
		runner.testFiles = new String[] {
				"C.java",
				"""
				@interface Ann { Class<? extends A> value(); }
				class A {}
				@Ann(C.B.class)
				class C<T extends java.util.List<Number>> {
				    class B extends A {}
				}
				"""};
		runner.runConformTest();
	}
	public void testGH1302() {
		Runner runner = new Runner();
		runner.testFiles = new String[] {
			"p/package-info.java",
			"""
			@org.eclipse.jdt.annotation.NonNullByDefault
			package p;
			""",
			"p/Parent.java",
			"""
			package p;
			import java.util.Map;
			public interface Parent {
			  Map<String, String> model();
			}
			""",
			"p/Child.java",
			"""
			package p;
			import java.util.Map;
			public record Child(Map<String, String> model) implements Parent {
			}
			"""
		};
		runner.customOptions = getCompilerOptions();
		runner.classLibraries = this.LIBS;
		runner.runConformTest();
	}

	public void testGH1691_a() {
		Runner runner = new Runner();
		runner.testFiles = new String[] {
			"bug/package-info.java",
			"""
			@org.eclipse.jdt.annotation.NonNullByDefault
			package bug;
			""",
			"bug/BlahSuper.java",
			"""
			package bug;

			import java.io.IOException;
			import java.io.OutputStream;

			public sealed interface BlahSuper<T, E extends Exception> permits Blah, BlahOther { }
			abstract non-sealed class BlahOther implements BlahSuper<OutputStream, IOException> { }
			""",
			"bug/Blah.java",
			"""
			package bug;

			import java.io.IOException;
			import java.io.OutputStream;

			public abstract non-sealed class Blah<T, E extends Exception> implements BlahSuper<T, E> {
				public abstract static class InnerBlah extends Blah<OutputStream, IOException> { }
			}
			"""
		};
		runner.classLibraries = this.LIBS;
		runner.runConformTest();
	}


	public void testGH1691_b() {
		// @NonNull on secondary bound is sufficient
		Runner runner = new Runner();
		runner.testFiles = new String[] {
			"bug/Marker.java",
			"""
			package bug;
			public interface Marker {}
			""",
			"bug/MyException.java",
			"""
			package bug;
			import java.io.IOException;
			public class MyException extends IOException implements Marker {}
			""",
			"bug/BlahSuper.java",
			"""
			package bug;

			import java.io.OutputStream;
			import org.eclipse.jdt.annotation.NonNullByDefault;

			@NonNullByDefault
			public sealed interface BlahSuper<T, E extends Exception & Marker> permits Blah, BlahOther { }
			@NonNullByDefault
			abstract non-sealed class BlahOther implements BlahSuper<OutputStream, MyException> { }
			""",
			"bug/Blah.java",
			"""
			package bug;

			import java.io.OutputStream;
			import org.eclipse.jdt.annotation.NonNull;

			public abstract non-sealed class Blah<T, E extends Exception & @NonNull Marker> implements BlahSuper<T, E> {
				public abstract static class InnerBlah extends Blah<OutputStream, @NonNull MyException> { }
			}
			"""
		};
		runner.classLibraries = this.LIBS;
		runner.runConformTest();
	}
}

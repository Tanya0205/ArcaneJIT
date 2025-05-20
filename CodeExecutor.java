import java.io.*;
import javax.tools.*;

public class CodeExecutor {

    public static String compileAndRun(String userMethod) {
        String className = "Temp";
        String fileName = className + ".java";

        // Wrap method body in full class structure
        String fullCode = generateJavaClass(userMethod);

        try {
            // 1. Write code to Temp.java (overwrite)
            FileWriter writer = new FileWriter(fileName);
            writer.write(fullCode);
            writer.close();

            // 2. Delete old class file if exists
            File classFile = new File(className + ".class");
            if (classFile.exists()) {
                classFile.delete();
            }

            // 3. Compile the Java code and capture compiler output
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            int result = compiler.run(null, null, new PrintStream(errorStream), fileName);

            if (result != 0) {
                return "Compilation Failed!\n\n" + errorStream.toString();
            }

            // 4. Run the compiled class using ProcessBuilder
            ProcessBuilder builder = new ProcessBuilder("java", "-cp", ".", className);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            reader.close();

            return output.toString();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Generates a minimal Java class with a main method containing user code
    private static String generateJavaClass(String methodBody) {
        return """
            public class Temp {
                public static void main(String[] args) {
                    %s
                }
            }
            """.formatted(methodBody);
    }

    // ADD THIS main METHOD TO TEST
    public static void main(String[] args) {
        String userCode = "System.out.println(\"Hello from user code!\");";
        String output = compileAndRun(userCode);
        System.out.println("Output:\n" + output);
    }
}

package de.unistuttgart.iaas.messaging.quantumservice;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QuantumServiceApplicationTests {

    @Test
    void testExec() throws IOException {
        Process p = Runtime.getRuntime().exec(new String[] {"python", "actions/main.py"});

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line + "\n");
        }
    }

    @Test
    void testExecViaFile() throws IOException {
        FileWriter fw = new FileWriter("actions/main.py");

        fw.write("import sys");
        fw.write(System.lineSeparator());
        fw.write("import numpytest");
        fw.write(System.lineSeparator());
        fw.write(System.lineSeparator());
        fw.write("params = {");
        fw.write(System.lineSeparator());
        fw.write("    \"apiToken\": sys.argv[1],");
        fw.write(System.lineSeparator());
        fw.write("    \"device\": sys.argv[2]");
        fw.write(System.lineSeparator());
        fw.write("}");
        fw.write(System.lineSeparator());
        fw.write(System.lineSeparator());
        fw.write("result = numpytest.main(params)");
        fw.write(System.lineSeparator());
        fw.write("print(result)");

        fw.close();
    }
}

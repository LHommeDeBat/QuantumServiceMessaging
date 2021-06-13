package de.unistuttgart.iaas.messaging.quantumservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QuantumServiceApplicationTests {

    @Test
    void testExec() throws IOException {
        String[] command = new String[]{"python", "actions/qiskittest.py", "agrar"};
        Process p = Runtime.getRuntime().exec(command);

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line + "\n");
        }
    }
}

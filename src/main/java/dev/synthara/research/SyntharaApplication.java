package dev.synthara.research;

import com.embabel.agent.config.annotation.EnableAgents;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAgents
public class SyntharaApplication {
    public static void main(String[] args) {
        try {
            Class<?> clazz = Class.forName("com.embabel.agent.core.internal.LlmOperations");
            System.out.println("=== REFLECTION: LlmOperations METHODS ===");
            for (java.lang.reflect.Method m : clazz.getDeclaredMethods()) {
                System.out.println("  Method: " + m.getName() + " | Returns: " + m.getReturnType().getName() + " | Params: " + java.util.Arrays.toString(m.getParameterTypes()));
            }
            System.out.println("=== END OF REFLECTION ===");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SpringApplication.run(SyntharaApplication.class, args);
    }
}

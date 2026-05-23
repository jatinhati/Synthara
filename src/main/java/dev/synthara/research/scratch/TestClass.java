package dev.synthara.research.scratch;

import com.embabel.agent.core.internal.LlmOperations;
import org.springframework.stereotype.Component;

@Component
public class TestClass {
    private final LlmOperations llmOperations;

    public TestClass(LlmOperations llmOperations) {
        this.llmOperations = llmOperations;
    }
}

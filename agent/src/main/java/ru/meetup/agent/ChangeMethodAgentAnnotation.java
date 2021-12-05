package ru.meetup.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class ChangeMethodAgentAnnotation {

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("Agent Change method started");
        new AgentBuilder.Default()
                .type(ElementMatchers.any())
//                .type(ElementMatchers.isAnnotatedWith(Instrumenting.class))
                .transform((builder, typeDescription, classLoader, module) -> builder
                        .method(ElementMatchers.named("getFullName"))
                        .intercept(FixedValue.value("Transformed full name (annotation)"))
                ).installOn(instrumentation);
    }
}

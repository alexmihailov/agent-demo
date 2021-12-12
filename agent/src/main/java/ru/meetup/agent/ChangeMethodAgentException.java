package ru.meetup.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.ExceptionMethod;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

public class ChangeMethodAgentException {

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("Agent Change method started");
        new AgentBuilder.Default()
                .type(ElementMatchers.any())
                .transform((builder, typeDescription, classLoader, module) -> builder
                        .method(ElementMatchers.named("getFullName"))
                        .intercept(ExceptionMethod.throwing(IOException.class))
                ).installOn(instrumentation);

    }
}

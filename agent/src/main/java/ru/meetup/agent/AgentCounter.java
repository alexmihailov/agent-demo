package ru.meetup.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class AgentCounter {

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("Agent Counter started");
        instrumentation.addTransformer(new CounterTransformer());
    }

    static class CounterTransformer implements ClassFileTransformer {

        private static int count = 0;

        @Override
        public byte[] transform(
                ClassLoader loader,
                String className,
                Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain,
                byte[] classfileBuffer
        ) {
            System.out.println("Load class: " + className.replaceAll("/", "."));
            System.out.printf("Loaded %s classes%n", ++count);
            return null;
        }
    }
}

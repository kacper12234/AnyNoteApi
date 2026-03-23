package com.betacom.anynoteapi;

import org.springframework.boot.SpringApplication;

public class TestAnyNoteApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(AnyNoteApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

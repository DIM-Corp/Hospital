module com.example.demo.app {

    exports com.example.demo.app;
    exports com.example.demo.controller;
    exports com.example.demo.view;

    requires kotlin.stdlib;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    requires org.jfxtras.styles.jmetro;

    requires java.desktop;
    requires java.sql;

    requires tornadofx;
    requires tornadofx.controlsfx;

    requires exposed.core;
    requires exposed.dao;
    requires exposed.jdbc;
}
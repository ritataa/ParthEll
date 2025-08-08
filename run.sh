#!/bin/bash
export DISPLAY=:0
java --module-path javafx-sdk-21.0.1/lib --add-modules javafx.controls,javafx.fxml -cp out Main

open module EksiDesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib.jdk8;
    requires org.jsoup;
    requires org.fxmisc.richtext;
    requires java.desktop;

    exports io.github.vimasig.eksidesktop.ui;
}
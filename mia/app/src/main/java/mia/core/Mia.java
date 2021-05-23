package mia.core;

public class Mia {

    public Mia() {
        new MiaWebserver().init("192.168.2.101", 80);
    }

}

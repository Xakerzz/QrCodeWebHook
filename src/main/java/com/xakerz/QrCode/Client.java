package com.xakerz.QrCode;

public class Client {
    long id;
    int counter;
    public Client(){
        this.counter = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}

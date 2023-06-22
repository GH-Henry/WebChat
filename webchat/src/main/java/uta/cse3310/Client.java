package uta.cse3310;

public class Client {
    private int id;
    private String name;
    private String msg;

    public Client(int id) {
        this.id = id;
        //this.name = name;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

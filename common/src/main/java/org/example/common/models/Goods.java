package org.example.common.models;

//тестовый товар
public class Goods {

    private int id;
    private String name;
    private float price;

    public Goods() {
    }

    public Goods(String name, float price) {
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Goods{id=" + id + ", name='" + name + "', price=" + price + '}';
    }
}

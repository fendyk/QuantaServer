package com.fendyk.utilities;

import java.util.ArrayList;

public class PayableCommand {

    String command;
    ArrayList<String> aliases;
    double price;
    long expires;
    double discountPercentage;

    public PayableCommand(
            String command,
            ArrayList<String> aliases,
            double price, long expires,
            double discountPercentage
    ) {
        this.command = command;
        this.aliases = aliases;
        this.price = price;
        this.expires = expires;
        this.discountPercentage = discountPercentage;
    }

    public String getCommand() {
        return command;
    }

    public ArrayList<String> getAliases() {
        return aliases;
    }

    public double getPrice() {
        return price;
    }

    public long getExpires() {
        return expires;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }
}

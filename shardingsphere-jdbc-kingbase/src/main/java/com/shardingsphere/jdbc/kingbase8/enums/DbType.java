package com.shardingsphere.jdbc.kingbase8.enums;


public enum DbType {

    KING_BASE8("kingbase8");

   private String name;

    DbType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

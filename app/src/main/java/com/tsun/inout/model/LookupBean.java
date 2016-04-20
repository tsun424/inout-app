package com.tsun.inout.model;

/**
 *	refer to table lookups
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	15-04-2016 16:12
 ************************************************************************
 *	update time			editor				updated information
 */
public class LookupBean {

    String id;
    String name;

    public LookupBean(){

    }

    public LookupBean(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return this.name;
    }
}


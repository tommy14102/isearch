package com.zznode.opentnms.isearch.otnRouteService.consts.enums;

public enum Rate {

    client("c"),
    odu0("0"),
    odu1("1"),
    odu2("2"),
    odu3("3"),
    odu4("4"),
    och("h");
    
    private String name;
    

    private Rate( String name  ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }



    @Override
    public String toString() {

        return String.valueOf(this.name + ":" + this.name );

    }
    
    

}

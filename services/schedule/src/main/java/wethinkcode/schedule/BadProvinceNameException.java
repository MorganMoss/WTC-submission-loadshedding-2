package wethinkcode.schedule;

public class BadProvinceNameException extends Exception{
    public BadProvinceNameException(){
        super("Bad province name requested");
    }
}

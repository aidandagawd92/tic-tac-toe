package tools;
//checks what version of Java you have, we all gotta be on the same JDK.

public class VersionCheck {
    public static void main(String[]args){
        System.out.println("java.version = " + System.getProperty("java.version"));
        System.out.println("java.vendor = " + System.getProperty("java.vendor"));
    }
}

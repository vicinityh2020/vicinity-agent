package sk.intersoft.vicinity.agent;


import org.restlet.data.Reference;

import java.util.Random;

public class JustTest {


    public int go(){
        Random r = new Random();
        int minimum = 20;
        int maximum = 30;
        return minimum + r.nextInt(maximum - minimum + 1);
    }

    public static void main(String[] args) throws Exception {
        JustTest t = new JustTest();
        System.out.println(t.go());
        System.out.println(t.go());
        System.out.println(t.go());
        System.out.println(t.go());
        System.out.println(t.go());
        System.out.println(t.go());
        System.out.println(t.go());
        System.out.println(t.go());
    }

}

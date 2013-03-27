package org.apache.servicemix.demo.simpleejb;

import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless
@Remote(SimpleGreeter.class )
public class SimpleGreeterBean implements SimpleGreeter {


    @Override
    public String greetMe(String name) {
        System.out.println("greetMe() invoked.");
        return "Welcome "+name + ", you have just invoked SimpleGreeterBean!";
    }
}

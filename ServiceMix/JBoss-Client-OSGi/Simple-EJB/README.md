## Simple EJB

This folder contains the stateless session bean that is to be deployed into
JBoss. It has been tested with JBoss 5.1.0.GA.

The EJB interface is very simple:

```java
public interface SimpleGreeter {

    String greetMe(String name);

}
```

and so is the implementation of this bean

```java
@Stateless
@Remote(SimpleGreeter.class )
public class SimpleGreeterBean implements SimpleGreeter {

    @Override
    public String greetMe(String name) {
        System.out.println("greetMe() invoked.");
        return "Welcome "+name + ", you have just invoked SimpleGreeterBean!";
    }
}
```


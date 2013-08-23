package demo;

import javax.ejb.Remote;

@Remote
public interface GreetingService {

  String generateGreeting(String name);
  
}

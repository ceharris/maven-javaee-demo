package demo.internal;

import javax.ejb.Local;

@Local
public interface GreetingRepository {

  String randomGreeting();
  
}

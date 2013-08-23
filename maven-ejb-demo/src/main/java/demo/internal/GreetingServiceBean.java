package demo.internal;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

import org.apache.commons.lang.StringUtils;

import demo.GreetingService;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class GreetingServiceBean implements GreetingService {

  @Override
  public String generateGreeting(String name) {
    if (StringUtils.isBlank(name)) {
      name = "world";
    }
    return String.format("Hello, %s", name);
  }

}

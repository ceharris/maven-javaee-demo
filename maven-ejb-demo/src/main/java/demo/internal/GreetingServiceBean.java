package demo.internal;

import java.text.MessageFormat;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.apache.commons.lang.StringUtils;

import demo.GreetingService;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class GreetingServiceBean implements GreetingService {

  @EJB
  private GreetingRepository repository;
  
  @Override
  public String generateGreeting(String name) {
    if (StringUtils.isBlank(name)) {
      name = "world";
    }
    return MessageFormat.format(repository.randomGreeting(), name);
  }

}

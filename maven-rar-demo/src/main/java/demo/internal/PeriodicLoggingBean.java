package demo.internal;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class PeriodicLoggingBean implements InitializingBean, DisposableBean {

  private static final Logger logger = 
      LoggerFactory.getLogger(PeriodicLoggingBean.class);

  private final Task task = new Task();
  private final Thread thread = new Thread(task, "Periodic Task");
  
  @PostConstruct
  public void afterPropertiesSet() {
    logger.info("started");
    thread.start();
  }
  
  @PreDestroy
  public void destroy() {
    logger.info("stopping");
    thread.interrupt();
    try {
      thread.join();
      logger.info("stopped");
    }
    catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      logger.warn("interrupted while waiting for service thread to exit");
    }
  }
  
  private class Task implements Runnable {

    @Override
    public void run() {
      while (!Thread.currentThread().isInterrupted()) {
        logger.info("task invoked");
        sleep(5000);
      }
    }
    
    private void sleep(long duration) {
      try {
        Thread.sleep(duration);
      }
      catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }
  }
  
}

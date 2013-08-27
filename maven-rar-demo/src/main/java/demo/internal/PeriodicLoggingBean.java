package demo.internal;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.UnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class PeriodicLoggingBean {

  private static final Logger logger = 
      LoggerFactory.getLogger(PeriodicLoggingBean.class);
  
  private final Runnable task = new LoggingTask();
  private final TimerTask scheduleWork = new ScheduleWorkTask();
  
  @Autowired
  private BootstrapContext bootstrapContext;

  @Autowired
  private ExecutorService executorService;
  
  private Timer timer;
  private boolean busy;
  
  
  @PostConstruct
  public void init() {
    try {
      timer = bootstrapContext.createTimer();
      timer.schedule(scheduleWork, 100, 100);
      logger.info("started");
    }
    catch (UnavailableException ex) {
      throw new IllegalStateException("cannot obtain timer");
    }
  }

  private synchronized boolean wasBusy() {
    if (busy) return true;
    busy = true;
    return false;
  }
  
  private synchronized void clearBusy() {
    busy = false;
  }
  
  private class ScheduleWorkTask extends TimerTask {

    private int count = 50;
    
    @Override
    public void run() {
      while (count > 0) {
        try {
          if (!wasBusy()) {
            executorService.submit(task);
            count--;
            logger.info("work scheduled");
          }
        }
        catch (RejectedExecutionException ex) {
          logger.error("can't schedule work: " + ex);
        }
      }
      
      executorService.shutdown();
      try {
        boolean terminated = executorService.awaitTermination(
            6000, TimeUnit.MILLISECONDS);
        if (terminated) {
          logger.info("executor shut down");
        }
        else {
          logger.warn("executor did not shut down");
        }
      }
      catch (InterruptedException ex) {
        logger.warn("interrupted while awaiting shutdown");
        Thread.currentThread().interrupt();
      }
      logger.info("done");
      timer.cancel();
    }
    
  }
  
  private class LoggingTask implements Runnable {

    @Override
    public void run() {
      Future<Integer> f = executorService.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          int delay = (int) (Math.random()*2000 + 1000);
          if (delay < 1500) {
            throw new UnsupportedOperationException("delay too small");
          }
          sleep(delay);
          return delay;
        } 
      });
      try {
        logger.info("work completed in {} milliseconds", f.get());
      }
      catch (CancellationException ex) {
        logger.warn("work cancelled");
      }
      catch (ExecutionException ex) {
        logger.warn("work failed: " + ex.getCause());
      }
      catch (InterruptedException ex) {
        logger.warn("work interrupted");
      }
      clearBusy();
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

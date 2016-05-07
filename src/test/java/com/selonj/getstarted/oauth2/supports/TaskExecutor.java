package com.selonj.getstarted.oauth2.supports;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TaskExecutor {
  private ExecutorService executor = Executors.newFixedThreadPool(10);

  public TaskExecutor() {
  }

  public TaskExecution spawns(final Runnable runner, int times) throws InterruptedException {
    final List<Future> futures = executor.invokeAll(tasks(runner, times));
    return new TaskExecution() {
      @Override public void waitUntilTimeout(long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        Iterator<Future> executions = futures.iterator();
        try {
          while (executions.hasNext()) executions.next().get(timeout, TimeUnit.MILLISECONDS);
        } finally {
          while (executions.hasNext()) executions.next().cancel(true);
        }
      }
    };
  }

  private List tasks(Runnable runner, int times) {
    List<Callable> tasks = new ArrayList<>();
    for (int i = 0; i < times; i++) tasks.add(task(runner));
    return tasks;
  }

  private Callable task(final Runnable runner) {
    return new Callable() {
      @Override public Object call() throws Exception {
        runner.run();
        return null;
      }
    };
  }

  public interface TaskExecution {
    void waitUntilTimeout(long timeout) throws InterruptedException, ExecutionException, TimeoutException;
  }
}
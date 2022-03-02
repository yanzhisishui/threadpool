package com.syc.threadpool.demo;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 测试 ThreadPoolExecutor 不足场景
 * */
public class ThreadPoolExecutorTest {

    public static void main(String[] args) {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(4, 4, 0,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(10));

        Task task1 = new Task("task1", pool);
        Task task2 = new Task("task2", pool);
        Task task3 = new Task("task3", pool);
        Task task4 = new Task("task4", pool);
        Task task5 = new Task("task5",  pool);
        Task task6 = new Task("task6",  pool);
        Task task7 = new Task("task7", pool);
        Task task8 = new Task("task8", pool);
        Task task9 = new Task("task9", pool);

        //添加依赖任务
        task1.dependentTasks.add(task2);
        task1.dependentTasks.add(task3);
        task1.dependentTasks.add(task4);
        task1.dependentTasks.add(task5);

        task2.dependentTasks.add(task6);
        task3.dependentTasks.add(task7);
        task4.dependentTasks.add(task8);
        task5.dependentTasks.add(task9);
        pool.submit(task1);
    }

    /**
     * 自定义 task 类，模拟依赖任务
     */
    static class Task implements Callable<String> {
        String name;
        ThreadPoolExecutor pool;

        List<Task> dependentTasks = new ArrayList<>();


        public Task(String name, ThreadPoolExecutor pool) {
            this.name = name;
            this.pool = pool;
        }

        @Override
        @SneakyThrows
        public String call() {
            List<Future<String>> futures = dependentTasks.stream()
                    .map(task -> {
                        System.out.println(Thread.currentThread().getId() + "-" + task.name + "-" + task);
                        return pool.submit(task);
                    })
                    .collect(Collectors.toList());

            for (Future<String> future : futures) {
                System.out.println("--" + future);
                future.get(); //程序将一直阻塞在这
            }
            System.out.println("任务：" + name + " 执行完毕");
            return "";
        }

    }
}



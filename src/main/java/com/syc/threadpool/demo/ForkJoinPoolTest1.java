package com.syc.threadpool.demo;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoinPool 解决任务依赖场景
 * */
public class ForkJoinPoolTest1 {
    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool(4);

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

        /**
         * doJoin
         * 判断当前任务的执行状态未完成
         *      当前线程是ForkJoin线程
         *          从工作队列中取出一个任务执行
         *      否则等待当前任务执行完毕
         * */
        pool.invoke(task1);
    }


    /**
     * 自定义 task 类，模拟依赖任务
     */
    static class Task extends RecursiveTask<String>  {
        String name;
        ForkJoinPool pool;

        List<Task> dependentTasks = new ArrayList<>();


        public Task(String name, ForkJoinPool pool) {
            this.name = name;
            this.pool = pool;
        }

        @Override
        public String toString() {
            return "Task{" +
                    "name='" + name + '\'' +
                    ", dependentTasks=" + dependentTasks +
                    '}';
        }

        @Override
        @SneakyThrows
        protected String compute() {

            //task1 --> task2.join --> compute --> task6.join
            dependentTasks.forEach(t-> {
                t.fork();
                System.out.println(this.name+"-"+t.name+"-"+Thread.currentThread().getName());;

            });//拆分结点任务
            dependentTasks.forEach(ForkJoinTask::join);//计算叶子结点任务并合并
            return "xxx";
        }
    }
}

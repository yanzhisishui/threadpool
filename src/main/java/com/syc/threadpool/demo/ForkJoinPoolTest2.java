package com.syc.threadpool.demo;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
/**
 * ForkJoinPool 解决任务分治
 * */
public class ForkJoinPoolTest2 {
    public static void main(String[] args) throws Exception {
        ForkJoinPool pool = new ForkJoinPool(8);
        Task task = new Task(1, 1000);
        pool.invoke(task);
    }

    static class Task extends RecursiveTask<Long> {
        static final int THRESHOLD = 20; //拆分因子
        int start;
        int end;

        Task(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            // 如果任务到达设定的最细粒度，直接执行计算
            if (end - start <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i <= end; i++) {
                    sum += i;
                }
                return sum;
            }
            // 任务太大,一分为二:
            int middle = (end + start) / 2;
            System.out.printf("split %d~%d ==> %d~%d, %d~%d%n", start, end, start, middle, middle+1, end);
            Task subtask1 = new Task(start, middle);
            Task subtask2 = new Task(middle + 1, end);
            invokeAll(subtask1, subtask2);
            Long result1 = subtask1.join();
            Long result2 = subtask2.join();
            Long result = result1 + result2;
            System.out.println("result = " + result1 + " + " + result + " ==> " + result);
            return result;
        }
    }
}
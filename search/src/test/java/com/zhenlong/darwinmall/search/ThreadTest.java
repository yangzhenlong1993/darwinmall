package com.zhenlong.darwinmall.search;

import java.util.concurrent.*;

public class ThreadTest {
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
/*        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("当前线程" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果" + i);
        }, executorService);*/

        /**
         * 方法!@!!!!!@#!@#!@#!成功!@#!@#$!@#$!@#$完成后的处理
         */
/*        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程" + Thread.currentThread().getId());
            int i = 10 / 0;
            System.out.println("运行结果" + i);
            return i;
        }, executorService).whenComplete((result, exception) -> {
            //虽然能得到异常信息，但是没法修改返回数据
            System.out.println("异步任务成功完成，结果是" + result + ",异常是" + exception);
        }).exceptionally(throwable -> {
            //发生异常时，返回一个默认值
            return 10;
        });*/

        /**
         * 方法执行完成后的处理
         */
/*        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程" + Thread.currentThread().getId());
            int i = 10 / 0;
            System.out.println("运行结果" + i);
            return i;
        }, executorService).handle((res, thr) -> {
            if (res != null) {
                return res * 2;
            }
            if (thr != null) {
                return 0;
            }
            return 0;
        });*/

        /**
         * 线程串行划 (带Async后缀的会新建一个线程异步执行任务)
         * thenRun：不能获取到上一步的执行结果，无返回值
         * thenRunAsync(()->{
         *                 System.out.println("任务2 启动了"))
         * thenAccept，可以获取上一步的执行结果，无返回值
         * .thenAcceptAsync(res->{
         *             System.out.println("上一次的结果"+res)
         */
        /*CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果" + i);
            return i;
        }, executorService).thenApplyAsync(res -> {
            System.out.println("上一步的结果" + res);
            return res * 2;
        }, executorService);*/
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程启动：" + Thread.currentThread().getId());
            int i = 10 / 2;
            return i;
        }, executorService);

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程启动：" + Thread.currentThread().getId());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "hello";
        }, executorService);

/*        future1.runAfterBothAsync(future2, ()->{
            System.out.println("两个任务执行之后合并");
        },executorService);*/

/*        future1.thenAcceptBothAsync(future2,(f1,f2)->{
            System.out.println("任务合并之后"+f1+";;"+f2);
        },executorService);*/

/*        CompletableFuture<String> stringCompletableFuture = future1.thenCombineAsync(future2, (f1, f2) -> {
            return f1 + "拼接" + f2;
        }, executorService);

        System.out.println(stringCompletableFuture.get());*/

        //AB两个任务只要有一个完成就执行C
        future1.runAfterEitherAsync(future2,()->{
            System.out.println("C任务执行完成");
        }, executorService);
    }

    public static void thread() throws ExecutionException, InterruptedException {
        //1.继承thread
        Thread01 thread01 = new Thread01();
        thread01.start();
        //2.实现runnable接口
        Runnable01 runnable01 = new Runnable01();
        new Thread(runnable01).start();
        //3.实现callable接口+futureTask
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
        new Thread(futureTask).start();
        Integer resultFromThread = futureTask.get();//阻塞等待整个线程执行完成，并获取返回结果
        System.out.println(resultFromThread);

        /**
         * 区别：1， 2不能得到返回值，3可以获取返回值
         *          1，2，3都不能控制线程资源，只有4可以，保证系统稳定，不会因为高并发而崩溃
         *         我们以后的业务代码中，以上三种启动线程的方式都不用，因为可能会导致资源耗尽，应该将所有的多线程异步任务都交给线程池执行
         *
         * 当前系统或业务中只有一两个线程池，每个异步任务提交给线程池，让线程池执行
         *  executorService.execute(new Runnable01());
         * 线程池的创建：
         * 1.Executors
         * 2.new ThreadPoolExecutor
         *    七大参数:1, corePoolSize：核心线程数[一直存在，除非设置允许线程超时]，创建好以后就准备就绪的线程数量，就等待来接受异步任务去执行， 5个 Thread thread = new Thread(); thread.start();
         *    2. maximumPoolSize: 最大线程数量， 控制资源
         *    3.存活时间，如果当前的线程数量大于核心线程数量，只要非核心线程的空闲时间大于设定值，就销毁该线程
         *    4.阻塞队列：如果任务有很多，就会将目前多的任务放在队列里面。只要有线程空闲，就会去队列里面中取出新的任务执行
         *    5.线程的创建工厂：可以自定义工厂
         *    6.处理器：如果队列满了，按照我们指定的拒绝策略拒绝任务的执行
         *工作顺序
         * 线程池创建，准备好core数量的核心线程，准备接受任务
         * 如果核心线程满了，就将再进来的任务放入阻塞队列中，空闲的核心就会自己去阻塞队列里执行任务
         * 阻塞队列满了，就直接开新线程执行，最大只能到指定的最大的线程数量
         * 如果最大线程数量都满了，就会用rejectedExecutionHandler拒绝任务
         * 非核心线程都执行完成，有很多空闲的话，在指定的存活时间之后，会释放非核心线程
         *LinkedBlockingDeque默认是Integer的最大值，可以存放这么多的任务。但是如果设置integer最大值，容易导致内存不够
         * 所以要根据业务实际情况写入固定的队列任务数，也可以用压力测试配合jvm监控软件调整数量
         */
        //4. 线程池，给线程池直接提交任务
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 200, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        Executors.newCachedThreadPool();


    }

    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果" + i);
        }
    }

    public static class Runnable01 implements Runnable {
        @Override
        public void run() {
            System.out.println("当前线程" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果" + i);
        }
    }

    public static class Callable01 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果" + i);
            return i;
        }
    }
}

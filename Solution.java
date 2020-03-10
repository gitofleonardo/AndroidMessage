import java.util.concurrent.ConcurrentLinkedQueue;

public class Solution{
    static class Message{
        private int what;
        private String msg;
        public Handler target;
    }
    static class Looper{
        private ConcurrentLinkedQueue<Message> mQueue;
        static final ThreadLocal<Looper> threadLocal=new ThreadLocal<>();
        private final Object lock=new Object();

        public Looper(){
            mQueue=new ConcurrentLinkedQueue<>();
            threadLocal.set(this);
        }
        public ConcurrentLinkedQueue<Message> getMessageQueue(){
            return mQueue;
        }
        public static Looper myLooper(){
            return threadLocal.get();
        }
        public void wake(){
            synchronized (lock){
                lock.notify();
            }
        }
        public void loop() throws InterruptedException {
            while (true){
                synchronized (mQueue){
                    if (mQueue.isEmpty()){
                        synchronized (lock){
                            lock.wait();
                        }
                    }
                    Message msg=mQueue.poll();
                    if (msg!=null && msg.target!=null){
                        msg.target.handleMessage(msg);
                    }
                }
            }
        }
    }
    static class Handler{
        private Looper looper;
        private ConcurrentLinkedQueue<Message> mQueue;

        public Handler(){
            looper=Looper.myLooper();
            mQueue=looper.getMessageQueue();
        }
        public void handleMessage(Message msg){
            System.out.println(msg.msg);
        }
        public void sendMessage(Message msg){
            msg.target=this;
            mQueue.add(msg);
            looper.wake();
        }
    }
    static class RunnableImp1 implements Runnable{
        public Handler handler;
        public RunnableImp1(Handler handler){
            this.handler=handler;
        }
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            }catch (Exception e){

            }
            while (true){
                Message msg=new Message();
                msg.what=0;
                msg.msg="hello world!Message from:"+Thread.currentThread().getName();
                handler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                }catch (Exception e){

                }
            }
        }
    }
    public static void main(String[] args) throws Exception{
        Looper looper=new Looper();
        Handler handler=new Handler();
        Thread t1=new Thread(new RunnableImp1(handler));
        Thread t2=new Thread(new RunnableImp1(handler));
        Thread t3=new Thread(new RunnableImp1(handler));

        t1.start();
        t2.start();
        t3.start();

        looper.loop();
    }
}

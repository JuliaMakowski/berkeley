package Server;

public class Clock extends Thread{


    private int shift;
    private int delay;
    public Clock(int delay,int shift){
        this.delay = delay;
        this.shift = shift;
    }


    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(1000);
                if (shift/5==0)Thread.sleep(delay);
                shift++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

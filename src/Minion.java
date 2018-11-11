import java.util.Queue;

public class Minion extends Thread {
    private static final Object SLEEPLOCK = "minionsleep";
    private static int numCreated;
    public static boolean allEnteredHouse;
    static Queue<Minion> kissQueue;
    static Queue<Minion> waitingAtTheDoor;
    static Alice alice;
    boolean waitingAtDoor;
    static int numAsleep;

    Minion() {
        super("Minion" + ++numCreated);
    }
    @Override
    public void run() {
        try {
            waitForKiss();
            System.out.println(getName() + ": Thank you Alice!");
            System.out.println(getName()  +" is going to work in the deli");
            sleep((long) (Math.random() * 10000));
            System.out.println(getName() + " is done working in the deli");
            goHome();
            System.out.println(getName() + " is going to play games");
            System.out.println(getName() + " wants to eat at the table");
            Main.tableSemaphore.acquire();
            System.out.println(getName() + "is at the table");
            sleep(100);
            Main.tableSemaphore.release();
            fallAsleep();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void fallAsleep() {
        System.out.println("trying to fall asleep");
        try {
            sleep((long) (Math.random()*3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName() + "going to sleep");
        synchronized (SLEEPLOCK)
        {
            numAsleep++;
            System.out.println(numAsleep + "minions are sleeping");
        }
    }
    private void waitForKiss() throws InterruptedException {
        System.out.println(getName() + " is waiting for a kiss.");
        synchronized (this) {
            kissQueue.add(this);
            wait();
        }


    }

    private void goHome() {
        System.out.println(getName() + "is getting on line at the door");
        synchronized (this)
        {
            waitingAtTheDoor.add(this);

        }
        if(waitingAtTheDoor.size()==Main.NUM_MINIONS)
        {
            System.out.println(getName() + "has the zuchus to knock on the door and wake up alice");
            while(!alice.isReadyForMinionsToComeHome());
            synchronized (alice) {
                alice.notify();
            }
        }
        synchronized (this)
        {
            waitingAtDoor = true;
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
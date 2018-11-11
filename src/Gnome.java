import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.Semaphore;

public class Gnome extends Thread {
    static int numAsleep;
    public static final String SLEEPLOCK = "gnomesleep";
    public static boolean allEnteredHouse;
    private static int numCreated;
    private static Queue<Gnome> leavingTheHouse;
    static Queue<Gnome> kissQueue;
    static Stack<Gnome> waitingToGoInAfterWork;
    static Alice alice;
    static Semaphore bathroom = new Semaphore(1);
    public boolean waitingAtDoor;

    Gnome() {
        super("Gnome" + ++numCreated);
    }

    @Override
    public void run() {
        try {
            waitForKiss();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitForKiss() throws InterruptedException {
        System.out.println(getName() + " is waiting for a kiss.");
        synchronized (this) {
            kissQueue.add(this);
            wait();
        }
        System.out.println(getName() + ": Have a good day!");
        System.out.println(getName() + " is going to work in the mine");
        sleep((long) (Math.random() * 10000));
        System.out.println(getName() + " is done working in the mine");
        goHome();
        System.out.println(getName() + " was let into the house and is going outside to play");
        sleep((long) (Math.random() * 10000));
        System.out.println(getName() + " wants to eat at the table");
        Main.tableSemaphore.acquire();
        System.out.println(getName() + "is at the table");
        sleep(100);
        Main.tableSemaphore.release();
        System.out.println(getName() + "wants to go to the bathroom");
        bathroom.acquire();
        System.out.println(getName() + " is in the bathroom");
        sleep(100);
        bathroom.release();


        fallAsleep();
    }

    private void fallAsleep() {
        System.out.println(getName() + "trying to fall asleep");
        try {
            sleep((long) (Math.random()*3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName() + "going to sleep");
        synchronized (SLEEPLOCK)
        {
            numAsleep++;
            System.out.println(numAsleep + "gnomes are sleeping");
        }
    }

    private void goHome() {
        System.out.println(getName() + "is getting on line at the door");
        synchronized (this) {
            waitingToGoInAfterWork.push(this);

        }
        if (waitingToGoInAfterWork.size() == Main.NUM_GNOMES) {
            System.out.println(getName() + "has the zuchus to knock on the door and wake up alice");
            while (!alice.isReadyForGnomesToComeHome())
            {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("alice isnt ready for the gnomes yet");
            }
            System.out.println(getName() + "sees that alice is ready for the gnomes");
            synchronized (alice) {
                alice.notify();
            }
        }
        synchronized (this) {
            waitingAtDoor = true;
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}



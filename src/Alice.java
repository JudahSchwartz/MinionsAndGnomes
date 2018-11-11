import java.util.Queue;
import java.util.Stack;

public class Alice extends Thread {
    public Queue<Gnome> gnomeKissQueue;
    public Queue<Minion> minionKissQueue;
    Bob bob;
    private boolean readyForMinionsToComeHome = false;
    private boolean readyForGnomesToComeHome = false;
    public Queue<Minion> minionWaitingAtDoor;
    public Stack<Gnome> gnomeWaitingAtDoor;
    public boolean isAsleep;

    public Alice() {
        super("Alice");
    }

    @Override
    public void run() {
        System.out.println(getName() + " is about to kiss the minions.");
        kissTheQueue(minionKissQueue, Main.NUM_MINIONS);
        System.out.println(getName() + " is about to kiss the gnomes.");
        kissTheQueue(gnomeKissQueue, Main.NUM_GNOMES);
        System.out.println(getName() + " is going to wake up bob");
        while (!bob.fellAsleep) ;
        synchronized (bob) {
            bob.notify();
        }
        System.out.println(getName() + " woke up bob");
        System.out.println(getName() + " is waiting at the door for the minions");
        synchronized (this) {
            readyForMinionsToComeHome = true;
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(getName() + " heard a knock at the door from the minions");
        for (Minion m : minionWaitingAtDoor) {
            while (!m.waitingAtDoor) ;//must wait for the minion that knocked to wait before calling notify
            synchronized (m) {
                m.notify();
            }
        }
        System.out.println(getName() + " just let all the minions in, and is now ready for the gnomes");
        Minion.allEnteredHouse = true;
        synchronized (this) {
            readyForGnomesToComeHome = true;
            try {
                System.out.println(getName() + " is ready and going to wait for the gnomes");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while(!gnomeWaitingAtDoor.empty()) {
            Gnome g = gnomeWaitingAtDoor.pop();
            while(!g.waitingAtDoor);
            synchronized (g){
                g.notify();
            }
        }
        System.out.println(getName() + " just let all the gnomes in");
        Gnome.allEnteredHouse = true;

        System.out.println(getName() + " is waiting for everyone to go to sleep");
        while(Gnome.numAsleep < Main.NUM_GNOMES || Minion.numAsleep < Main.NUM_GNOMES)
        {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(getName() + " sees not everyone is asleep");
        }
        System.out.println(getName() + " sees everyone is asleep and is going to read");
        goIntoRoom();

    }
    private void goIntoRoom()
    {
        Main.aliceInRoom = true;
        System.out.println(getName() + " is in the room reading");
        try {
            sleep((long) (1000 * Math.random()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName() + " is going to sleep");
        isAsleep = true;
    }
    private <T extends Thread> void kissTheQueue(Queue<T> q, int numToWaitFor) {
        int counter = 0;
        while (counter < numToWaitFor) {
            while (q.isEmpty()) ;
            T min = q.remove();
            System.out.println(getName() + " kissed " + min.getName());
            synchronized (min) {
                min.notify();
            }
            counter++;
        }
    }

    public boolean isReadyForMinionsToComeHome() {
        return readyForMinionsToComeHome;
    }

    public boolean isReadyForGnomesToComeHome()
    {
        return readyForGnomesToComeHome;
    }
}

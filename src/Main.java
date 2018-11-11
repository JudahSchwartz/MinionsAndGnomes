import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
public class Main {
    public static  boolean bobInRoom = false;
    public static boolean aliceInRoom = false;
    public static boolean lightsOn = false;
    public int numOnCouch = 0;
    static final int NUM_GNOMES = 7;
    static final int NUM_MINIONS = 10;
    static final Semaphore tableSemaphore = new Semaphore(5);
    public static void main(String[] args) {
        Alice alice = new Alice();
        Minion.alice = Gnome.alice = alice;
        Bob bob = new Bob();
        alice.bob = bob;
        Queue<Gnome> gnomeKissQ = Gnome.kissQueue= alice.gnomeKissQueue = new ConcurrentLinkedQueue<>();
        Queue<Minion> minionKissQ = Minion.kissQueue = alice.minionKissQueue =new ConcurrentLinkedQueue<>();
        Queue<Minion> waitingAtTheDoor = Minion.waitingAtTheDoor = alice.minionWaitingAtDoor = new ConcurrentLinkedQueue<>();
        /* the default java.util.stack is actually thread-safe since
        it extends vector, but I saw a lot of warnings against using it. I might write a new DS to support it*/
        Stack<Gnome> gnomesWaitingAtDoor = Gnome.waitingToGoInAfterWork = alice.gnomeWaitingAtDoor = new Stack<>();
        bob.start();
        alice.start();
        Gnome[] gnomeArray = new Gnome[NUM_GNOMES];
        Minion[] minionArray = new Minion[NUM_MINIONS];
        for (int i = 0; i < NUM_GNOMES; i++) {
            gnomeArray[i] = new Gnome();
            gnomeArray[i].start();
        }
        for (int i = 0; i < NUM_MINIONS; i++) {
            minionArray[i] = new Minion();
            minionArray[i].start();
        }
        while(!bothAsleep(alice,bob))
        {
            if((bobInRoom || aliceInRoom) )
            {
                if(!lightsOn) {
                    System.out.println("somone is in the room, lights go on");
                    lightsOn = true;
                }
            }
            else
            {
                if(lightsOn){
                    System.out.println("nobody is in the room. Lights go off");
                    lightsOn = false;
                }
            }

        }
        System.out.println("alice and bob went to sleep. lights off and we're done");
        lightsOn = false;

    }

    private static boolean bothAsleep(Alice alice, Bob bob) {
        boolean bothAsleep = alice.isAsleep && bob.isAsleep;
        if(!bothAsleep) {
            System.out.println("They are not both sleeping");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return bothAsleep;

    }
}

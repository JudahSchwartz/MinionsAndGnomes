public class Bob extends Thread {
    boolean fellAsleep = false;
    boolean isAsleep;

    public Bob()
    {
        super("Bob");
    }
    private void goIntoRoom()
    {
        Main.bobInRoom = true;
        System.out.println(getName() + " is in the room reading");
        try {
            sleep((long) (1000 * Math.random()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName() + " is going to sleep");
        isAsleep = true;
    }
    @Override public void run()
    {
        System.out.println(getName() + "going to sleep until alice wakes me up");
        synchronized (this)
        {
            fellAsleep = true;
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(getName() + "just got woken up");
            System.out.println(getName() + "is going to do some accounting");
            try {
                sleep((long) (Math.random() * 10000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(getName() + " going to wait for all minions and gnomes to go in");
            while(!Gnome.allEnteredHouse || !Minion.allEnteredHouse)
            {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(getName() + "sees they arent all in");
            }
            System.out.println(getName() + " sees that everyone is in the house, going in");
            System.out.println(getName() + " wants to eat at the table");
            try {
                Main.tableSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(getName() + "is at the table");
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Main.tableSemaphore.release();

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
            System.out.println("Everyone is asleep going. " + getName() + "going to read.");
            goIntoRoom();
        }

    }
}

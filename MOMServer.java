import exceptions.EMomError;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MOMServer extends MOMServant {
    public MOMServer() throws EMomError {}

    private static int PORT = 1099;

    public static void main(String[] args) {

        if (args.length > 0) {
            PORT = Integer.parseInt(args[0]);
        }

        try {
            System.out.println("Starting MOM Server...");
            MOMServant mom = new MOMServant();
            MOM momStub = (MOM) UnicastRemoteObject.exportObject(mom, 0);

            // Create the JNDI registry
            Registry registry = LocateRegistry.createRegistry(PORT);
            System.out.println("JNDI registry created on port "+ PORT);

            // Register the stub in the registry
            registry.rebind("MOM", momStub);

            System.out.println("MOM Server up!");

        } catch (Exception e) {
            System.out.println("Error in server > " + e.getMessage());
        }
    }
}

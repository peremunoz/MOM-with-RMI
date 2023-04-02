import exceptions.EMomError;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.System.exit;

public class MOMServer extends MOMServant {
    public MOMServer() throws EMomError {}

    private static int PORT = 1099;

    public static void main(String[] args) {

        if (args.length > 0) {
            PORT = Integer.parseInt(args[0]);
        }

        // Set the security manager
        System.setSecurityManager(new SecurityManager());

        System.out.println("Starting MOM Server...");

        MsgQ_Init();

        System.out.println("MOM Server up ✓");
    }

    private static void MsgQ_Init() {
        try {
            MOMServant mom = new MOMServant();
            MOM momStub = (MOM) UnicastRemoteObject.exportObject(mom, 0);

            // Create the RMI registry
            Registry registry = LocateRegistry.createRegistry(PORT);
            System.out.println("RMI registry created on port "+ PORT + " ✓");

            // Register the stub in the registry
            registry.rebind("MOM", momStub);
        } catch (Exception e) {
            System.out.println("Error starting the server → " + e.getMessage());
            exit(1);
        }
    }
}

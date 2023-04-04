# Implementing the MOM paradigm in Java, using RMI
 ## Introduction
This project is a Java implementation of the MOM paradigm, using RMI. It is a project for the Distributed Computation and Applications subject at the Universitat de Lleida.
 
 ## Installation ⚙️
To install this project, you need to have Java installed in your computer. You can download it from [here](https://www.java.com/es/download/).
     
## Usage ⏯️
First of all, you will need to compile the project. To do so, you can use the following command:\
``` javac *.java ```

Then, you can start the server by running the following command:\
``` java MOMServer [<port>] ```

- Where ```<port>``` is the port where the server will be listening for connections.

After the server is started correctly, you can start the clients by running the following command:
- For master client: ``` java DisSumMaster <number_to_sum_up> <number_of_workers> [<server_ip>] ```
- For worker client: ``` java DisSumWorker [<server_ip>] ```
- Where ```<number_to_sum_up>``` is the number that will be summed up by the workers, ```<number_of_workers>``` is the number of workers that will be used to sum up the number, ```<server_ip>``` is the IP address of the server.
- If the server IP is not specified, the client will try to connect to the server in the local machine.

When the clients are started, the master client will send the number to the server, and the server will send it to the workers. The workers will sum up the number and send the result to the server, which will send it to the master client. The master client will print the result.

Note: If you are executing the application in a linux machine, you can use the ```run.sh``` script to compile and run the application. You can use the following command to do so:\
``` ./run.sh [<number_to_sum_up>] [<number_of_workers>] [<server_ip>] [<server_port>]```\
All the parameters are optional. If you don't specify any parameter, the script will use the default values.

## Contributing
If you want to contribute to this project, you can do so by forking the project and creating a pull request with your changes.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits
This project was developed by Pere Muñoz Figuerol - @peremunoz
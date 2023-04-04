# Compile all the .java files in the current directory
# and run the application

# Expect four arguments:
# 1. The top number to sum up to (default: 1000)
# 2. The number of workers (default: 2)
# 3. The ip address of the server (default: localhost)
# 4. The port number of the server (default: 1099)

# Set the default values
top=1000
workers=2
ip=localhost
port=1099

# If the user has specified a top number, use it
if [ $# -gt 0 ]; then
    top=$1
fi

# If the user has specified a number of workers, use it
if [ $# -gt 1 ]; then
    workers=$2
fi

# If the user has specified an ip address, use it
if [ $# -gt 2 ]; then
    ip=$3
fi

# If the user has specified a port number, use it
if [ $# -gt 3 ]; then
    port=$4
fi

# Create a variable for storing the path to the current directory
path=$(pwd)

# Compile all the .java files in the current directory
javac *.java

# Run the server
gnome-terminal --title=SERVER -- /bin/sh -c "java -Djava.security.manager=allow -Djava.security.policy=java.policy MOMServer $port; bash"

# Wait for the server to start
sleep 1

# Run the master in a new terminal
gnome-terminal --title=MASTER -- /bin/sh -c "java -Djava.security.manager=allow -Djava.security.policy=java.policy DisSumMaster $top $workers $ip; bash"

sleep 1

# Run workers in new terminal windows
for i in `seq 1 $workers`
do
    gnome-terminal --title=WORKER$i -- /bin/sh -c "java -Djava.security.manager=allow -Djava.security.policy=java.policy DisSumWorker $ip; bash"
done

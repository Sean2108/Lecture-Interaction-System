# Lecture-Interaction-System
System that analyzes students' responses to lecturers' questions to look for common misconceptions, and allows students to ask questions to the lecturers.

build jar using: 
```shell
mvn package
```
OR 
```shell
mvn clean install (must have maven installed)
```
run server using: 
```shell
sh runServer.sh
```

run client using: 
```shell
sh runClient.sh
```

server page is on port 4567, client page is on port 4568.

1. Run the server
2. Visit localhost:4567/ on your browser and select the relevant options. 
3. After the message that the question has been sent to the students is shown, start the client and visit localhost:4568. You will be prompted for an IP address. Input 127.0.0.1 if you are running locally (server and client on the same address), or refer to the server's IP address displayed on the confirmation page to connect to the server. (If connection error occurs, uncomment line 22 of VoterServer.java and replace hostname with your private IP. Connection seems to work intermittently with Linux depending on configurations due to inconsistency of InetAddress class)
4. Answer the question.
5. Return to the server side page that was left open before running the client (localhost:4567/confirm) and click view chart.

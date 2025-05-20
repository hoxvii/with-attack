/**

This class creates the server where two players can play together.
It reads and sends data to synchronize the frames of each player.

@author Joshua Patrick I. Bandola (240499) Carl A. Basco (240558)
@version 20 May 2024

I have not discussed the Java language code in my program
with anyone other than my instructor or the teaching assistants
assigned to this course.

I have not used Java language code obtained from another student,
or any other unauthorized source, either modified or unmodified.
If any Java language code or documentation used in my program
was obtained from another source, such as a textbook or website,
that has been clearly noted with a proper citation in the comments
of my program.

**/

import java.io.*;
import java.net.*;

public class GameServer {

    private ServerSocket ss;
    private int numPlayers;
    private int maxPlayers;
    
    private Socket p1Socket;
    private Socket p2Socket;
    private ReadFromClient p1ReadRunnable;
    private ReadFromClient p2ReadRunnable;
    private WriteToClient p1WriteRunnable;
    private WriteToClient p2WriteRunnable;

    private double p1x, p1y, p2x, p2y;
    private int p1xOffset, p2xOffset;

    private boolean p1moving, p2moving, p1punching, p2punching, 
    p1kicking, p2kicking, p1oppPunching, p2oppPunching, p1running, 
    p2running, p1crouching, p2crouching, p1left, p2left, p1right, p2right;

    public GameServer() {
        System.out.println("===== GAME SERVER =====");
        numPlayers = 0;
        maxPlayers = 2;

        p1x = 0;
        p1y = 0;
        p2x = 0;
        p2y = 0;

        try {
            ss = new ServerSocket(24049);
        } catch (IOException ex) {
            System.err.println("IOException from GameServer constructor");
        }
    }

    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");

            while (numPlayers < maxPlayers) {
                Socket s = ss.accept();
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                numPlayers++;
                out.writeInt(numPlayers);
                System.out.println("Player #" + numPlayers + " has connected.");

                ReadFromClient rfc = new ReadFromClient(numPlayers, in);
                WriteToClient wtc = new WriteToClient(numPlayers, out);

                if (numPlayers == 1) {
                    p1Socket = s;
                    p1ReadRunnable = rfc;
                    p1WriteRunnable = wtc;
                } else {
                    p2Socket = s;
                    p2ReadRunnable = rfc;
                    p2WriteRunnable = wtc;
                    p1WriteRunnable.sendStartMsg();
                    p2WriteRunnable.sendStartMsg();
                    Thread readThread1 = new Thread(p1ReadRunnable);
                    Thread readThread2 = new Thread(p2ReadRunnable);
                    readThread1.start();
                    readThread2.start();
                    Thread writeThread1 = new Thread(p1WriteRunnable);
                    Thread writeThread2 = new Thread(p2WriteRunnable);
                    writeThread1.start();
                    writeThread2.start();
                }
            }

            System.out.println("No longer accepting connections");

        } catch (IOException ex) {
            System.err.println("IOException from acceptConnections()");
        }
    }

    private class ReadFromClient implements Runnable {

        private int playerID;
        private DataInputStream dataIn;
        
        public ReadFromClient(int pid, DataInputStream in) {
            playerID = pid;
            dataIn = in;
            System.out.println("RFC" + playerID + " Runnable created");
        }

        @Override
        public void run() {
            try {
                while(true) {
                    if (playerID == 1) {
                        p1x = dataIn.readDouble();
                        p1y = dataIn.readDouble();
                        p1moving = dataIn.readBoolean();
                        p1punching = dataIn.readBoolean();
                        p1kicking = dataIn.readBoolean();
                        p1oppPunching = dataIn.readBoolean();
                        p1running = dataIn.readBoolean();
                        p1crouching = dataIn.readBoolean();
                        p1xOffset = dataIn.readInt();
                        p1left = dataIn.readBoolean();
                        p1right = dataIn.readBoolean();
                    } else {
                        p2x = dataIn.readDouble();
                        p2y = dataIn.readDouble();
                        p2moving = dataIn.readBoolean();
                        p2punching = dataIn.readBoolean();
                        p2kicking = dataIn.readBoolean();
                        p2oppPunching = dataIn.readBoolean();
                        p2running = dataIn.readBoolean();
                        p2crouching = dataIn.readBoolean();
                        p2xOffset = dataIn.readInt();
                        p2left = dataIn.readBoolean();
                        p2right = dataIn.readBoolean();
                    }
                }
            } catch (IOException ex) {
                System.err.println("IOException from RFC run()");
            }
        }
    }

    private class WriteToClient implements Runnable {

        private int playerID;
        private DataOutputStream dataOut;
        
        public WriteToClient(int pid, DataOutputStream out) {
            playerID = pid;
            dataOut = out;
            System.out.println("WTC" + playerID + " Runnable created");
        }

        @Override
        public void run() {
            try {
                while(true) {
                    if (playerID == 1) {
                        dataOut.writeDouble(p2x);
                        dataOut.writeDouble(p2y);
                        dataOut.writeBoolean(p2moving);
                        dataOut.writeBoolean(p2punching);
                        dataOut.writeBoolean(p2kicking);
                        dataOut.writeBoolean(p2oppPunching);
                        dataOut.writeBoolean(p2running);
                        dataOut.writeBoolean(p2crouching);
                        dataOut.writeInt(p2xOffset);
                        dataOut.writeBoolean(p2left);
                        dataOut.writeBoolean(p2right);
                        dataOut.flush();
                    } else {
                        dataOut.writeDouble(p1x);
                        dataOut.writeDouble(p1y);
                        dataOut.writeBoolean(p1moving);
                        dataOut.writeBoolean(p1punching);
                        dataOut.writeBoolean(p1kicking);
                        dataOut.writeBoolean(p1oppPunching);
                        dataOut.writeBoolean(p1running);
                        dataOut.writeBoolean(p1crouching);
                        dataOut.writeInt(p1xOffset);
                        dataOut.writeBoolean(p1left);
                        dataOut.writeBoolean(p1right);
                        dataOut.flush();
                    }
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ex) {
                        System.err.println("InterruptedException from WTC run()");
                    }
                }
            } catch (IOException ex) {
                System.err.println("IOException from WTC run()");
            }
        }

        public void sendStartMsg() {
            try {
                dataOut.writeUTF("We now have 2 players. Go!");
            } catch (IOException ex) {
                System.err.println("IOException from senStartMsg()");
            }
        }
    }

    
    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections(); 
    }

}
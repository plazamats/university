package server;

import dao.StudentDAO;
import domain.Student;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

public class ServerApp
{
    private ServerSocket listener;
    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private StudentDAO studentDAO;

    public ServerApp()
    {
        try {
            listener = new ServerSocket(6666, 1);
        }
        catch (IOException ioe)
        {
          System.out.println("IO Exception: " + ioe.getMessage());
        }
    }

    public void listen()
    {
        try {
          while (true)
          {
              System.out.println("Server is listening");
              client = listener.accept();
              System.out.println("Client connection has been accepted");

              processClient();
          }
        }
        catch(IOException ioe)
        {
            System.out.println("IO Exception: " + ioe.getMessage());
        }
    }

    public void processClient()
    {
        System.out.println("Creating input and output streams on Server");
        try {
            studentDAO = new StudentDAO();
            out = new ObjectOutputStream(client.getOutputStream());
            out.flush();
            in = new ObjectInputStream(client.getInputStream());

            boolean exit = false;
            while (!exit)
            {
                System.out.println("Reading request object from client");
                Object request = in.readObject();

                if (request instanceof Student)
                {
                    System.out.println("From CLIENT>> add " + request);
                    studentDAO.save((Student)request);
                }
                else if (request instanceof String)
                {
                    String msg = (String)request;
                    System.out.println("From CLIENT>> " + msg);

                    if (msg.equalsIgnoreCase("retrieve"))
                    {
                        ArrayList<Student> studentList = studentDAO.getAll();
                        System.out.println("Writing " + studentList.size() + " records to client");
                        out.writeObject(studentList);
                        out.flush();
                    }
                    else if (msg.equalsIgnoreCase("exit"))
                    {
                        out.writeObject("Goodbye");
                        out.flush();
                        exit = true;
                    }
                }
            }
        }
        catch (IOException ioe)
        {
            System.out.println("IO Exception: " + ioe.getMessage());
        }
        catch (ClassNotFoundException cnfe)
        {
            System.out.println("Class not found: " + cnfe.getMessage());
        }
        finally
        {
            closeConnections();
        }
    }

    private void closeConnections()
    {
        try {
            if (studentDAO != null)
                studentDAO.closeConnection();
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (client != null)
                client.close();
        }
        catch (IOException ioe)
        {
            System.out.println("IO Exception: " + ioe.getMessage());
        }
    }

    public static void main(String[] args)
    {
        ServerApp server = new ServerApp();

        server.listen();
    }
}

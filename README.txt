ADP262S Networking with JDBC Prac 4 (Student Records)

Two separate Maven NetBeans projects:
  StudentServer  packages: connection, domain, dao, server
  StudentClient  packages: domain, gui

SETUP (same as the Week 1 slides)
1. NetBeans > Services > Java DB > Create Database
   Name: University   Username: administrator   Password: admin
   (skip if you already have this database from earlier pracs)
2. Right-click the University database > Connect. Leave the Java DB server running.
   The StudentRecord table is created by the server on first run if it does not exist.
3. Open both projects in NetBeans (File > Open Project). If the Derby driver is not
   found, add the Derby client library (derbyclient) to the StudentServer project.

RUN ORDER
1. Run StudentServer (server.ServerApp). It prints "Server is listening".
2. Run StudentClient (gui.StudentRecordClient).
3. Add records, click Retrieve Records. Stop and restart the server: Retrieve still
   shows the earlier records because they are read from the database table.

PROTOCOL
  Client sends a Student object   -> server inserts it with a PreparedStatement
  Client sends the String "retrieve" -> server replies with ArrayList<Student> from the table
  Client sends the String "exit"  -> server replies "Goodbye", both sides close

NOTE: domain.Student must be identical, and in the same package name, in both projects.

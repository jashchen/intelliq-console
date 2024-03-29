package FTPSApps;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import java.io.*;
import java.security.NoSuchAlgorithmException;

// 使用apache commons-net库访问 ftps服务器


public class Apps {
  public static final String USAGE =
      "Usage: ftp [-s] [-b] <hostname> <username> <password> <remote file> <local file>\n" +
          "\nDefault behavior is to download a file and use ASCII transfer mode.\n" +
          "\t-s store file on server (upload)\n" +
          "\t-b use binary transfer mode\n";

  public  static void  main(String[] args) throws NoSuchAlgorithmException
  {
    int base = 0;
    boolean storeFile = false, binaryTransfer = false, error = false;
    String server, username, password, remote, local;
    String protocol = "SSL";    // SSL/TLS
    FTPSClient ftps;

    for (base = 0; base < args.length; base++)
    {
      if (args[base].startsWith("-s"))
        storeFile = true;
      else if (args[base].startsWith("-b"))
        binaryTransfer = true;
      else
        break;
    }

    if ((args.length - base) != 5)
    {
      System.err.println(USAGE);
      System.exit(1);
    }

    server = args[base++];
    username = args[base++];
    password = args[base++];
    remote = args[base++];
    local = args[base];

    ftps = new FTPSClient(protocol);

    ftps.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

    try
    {
      int reply;

      ftps.connect(server);
      System.out.println("Connected to " + server + ".");

      // After connection attempt, you should check the reply code to verify
      // success.
      reply = ftps.getReplyCode();

      if (!FTPReply.isPositiveCompletion(reply))
      {
        ftps.disconnect();
        System.err.println("FTP server refused connection.");
        System.exit(1);
      }
    }
    catch (IOException e)
    {
      if (ftps.isConnected())
      {
        try
        {
          ftps.disconnect();
        }
        catch (IOException f)
        {
          // do nothing
        }
      }
      System.err.println("Could not connect to server.");
      e.printStackTrace();
      System.exit(1);
    }

    __main:
    try
    {
      //ftps.setBufferSize(1000);

      if (!ftps.login(username, password))
      {
        ftps.logout();
        error = true;
        break __main;
      }


      System.out.println("Remote system is " + ftps.getSystemName());

      if (binaryTransfer) ftps.setFileType(FTP.BINARY_FILE_TYPE);

      // Use passive mode as default because most of us are
      // behind firewalls these days.
      ftps.enterLocalPassiveMode();
      ftps.execPROT("P");
      if (storeFile)
      {
        InputStream input;

        input = new FileInputStream(local);

        ftps.storeFile(remote, input);

        input.close();
      }
      else
      {
        OutputStream output;

        output = new FileOutputStream(local);

        ftps.retrieveFile(remote, output);

        output.close();
      }

      //ftps.logout();
    }
    catch (FTPConnectionClosedException e)
    {
      error = true;
      System.err.println("Server closed connection.");
      e.printStackTrace();
    }
    catch (IOException e)
    {
      error = true;
      e.printStackTrace();
    }
    finally
    {
//      if (ftps.isConnected())
//      {
//        try
//        {
//          ftps.disconnect();
//        }
//        catch (IOException f)
//        {
//          // do nothing
//        }
//      }
    }

    System.exit(error ? 1 : 0);
  } // end main
}

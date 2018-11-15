package ijarvis.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;

import java.io.IOException;

public class Apps {
    public static void main(String[] args) throws IOException, MyException {
        String conf_filename="/home/parallels/workspace/Javaworkspace/intelliq-console/src/main/resources/fdfs_client.conf";
        ClientGlobal.init(conf_filename);
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        String[] fileid=storageClient.upload_file("/home/parallels/workspace/Javaworkspace/intelliq-console/src/main/resources/images/images3.jpg","jpg",null);
        System.out.println(fileid.length);
        System.out.println(fileid[0]);
        System.out.println(fileid[1]);

    }
}

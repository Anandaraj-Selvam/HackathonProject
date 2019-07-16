using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Blob;
using Microsoft.WindowsAzure.Storage.RetryPolicies;
using System;
using System.IO;
using System.Web;

namespace ImageDetector.Helper
{
    public class Helper
    {
        private static CloudStorageAccount account = CloudStorageAccount.Parse("DefaultEndpointsProtocol=https;AccountName=hackimage;AccountKey=Ek7pEkD1dScx/ytgE5SJWl5x0u3jDAPzvBzEkeUwCme5v/6obRZsaSqYIfUFNmqccXv00LA2svfaMWNU2lpGtA==;EndpointSuffix=core.windows.net");
        private static CloudBlobClient blobClient = account.CreateCloudBlobClient();

        public bool WriteBlobData(string containerName, string blobName, HttpPostedFile postedFile)
        {
            try
            {
                blobClient.DefaultRequestOptions.RetryPolicy = new ExponentialRetry(TimeSpan.FromSeconds(1), 3);
                CloudBlobContainer container = blobClient.GetContainerReference(containerName);
                container.CreateIfNotExists();
                DateTime dateTime = DateTime.Now.Date;
                string folderPath = "";
                if (postedFile.ContentType.Contains("video"))
                    folderPath = "Videos";
                else if (postedFile.ContentType.Contains("image"))
                    folderPath = "Images";
                else if (postedFile.ContentType.Contains("audio"))
                    folderPath = "audio";
               // CloudBlobDirectory directory = container.GetDirectoryReference(dateTime.ToString("dd/MM/yyyy"));
                CloudBlockBlob blob = container.GetBlockBlobReference(folderPath+"/"+(DateTime.Now.ToString("dd-MM-yyyy"))+"/"+DateTime.Now.ToFileTime().ToString());
                blob.Properties.ContentType = postedFile.ContentType;
                container.SetPermissions(new BlobContainerPermissions { PublicAccess = BlobContainerPublicAccessType.Blob });
                postedFile.SaveAs(System.IO.Path.GetTempPath() + postedFile.FileName);
                using (Stream file = System.IO.File.OpenRead(System.IO.Path.GetTempPath() + postedFile.FileName))
                {
                    blob.UploadFromStream(file);

                }
                return true;
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
                return false;
            }
            finally
            {
                if (File.Exists(System.IO.Path.GetTempPath() + postedFile.FileName))
                    File.Delete(System.IO.Path.GetTempPath() + postedFile.FileName);
            }
        }




    }
}

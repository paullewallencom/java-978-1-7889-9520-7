package com.javacookbook.app;

import com.beust.jcommander.Parameter;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.common.resources.DL4JResources;
import org.deeplearning4j.common.resources.ResourceType;
import org.deeplearning4j.datasets.fetchers.TinyImageNetFetcher;
import org.deeplearning4j.spark.util.SparkDataUtils;

import java.io.File;
import java.io.FileNotFoundException;

/*
  Use this source code to pre-process the data and save the batch files to your local disk.
  You would need to manually transfer them to HDFS.
 */
public class PreProcessLocal {
    //The directory in which you would like to save your batches.
    private String localSaveDir = "{PATH-TO-SAVE-PREPROCESSED-DATASET}";

    @Parameter(names = {"--batchSize"}, description = "Batch size for saving the data", required = false)
    private int batchSize = 32;

    public static void main(String[] args) throws Exception {
        new PreProcessLocal().entryPoint(args);
    }

    protected void entryPoint(String[] args) throws Exception {

        if(localSaveDir.equals("{PATH-TO-SAVE-PREPROCESSED-DATASET}")){
            System.out.println("Please specify the directory in which pre-processed dataset needs to be stored");
            System.exit(0);
        }

        TinyImageNetFetcher f = new TinyImageNetFetcher();
        f.downloadAndExtract();

        //Preprocess the training set
        File baseDirTrain = DL4JResources.getDirectory(ResourceType.DATASET, f.localCacheName() + "/train");
        File saveDirTrain = new File(localSaveDir, "train");
        if(!saveDirTrain.exists())
            saveDirTrain.mkdirs();
        SparkDataUtils.createFileBatchesLocal(baseDirTrain, NativeImageLoader.ALLOWED_FORMATS, true, saveDirTrain, batchSize);

        //Preprocess the test set
        File baseDirTest = DL4JResources.getDirectory(ResourceType.DATASET, f.localCacheName() + "/test");
        File saveDirTest = new File(localSaveDir, "test");
        if(!saveDirTest.exists())
            saveDirTest.mkdirs();
        SparkDataUtils.createFileBatchesLocal(baseDirTest, NativeImageLoader.ALLOWED_FORMATS, true, saveDirTest, batchSize);

        System.out.println("----- Data Preprocessing Complete -----");
    }

}

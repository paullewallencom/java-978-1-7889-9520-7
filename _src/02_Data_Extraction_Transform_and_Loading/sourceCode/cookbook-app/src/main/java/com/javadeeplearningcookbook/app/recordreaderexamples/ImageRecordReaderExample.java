package com.javadeeplearningcookbook.app.recordreaderexamples;

import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageRecordReaderExample {
    public static void main(String[] args){

        /*
        *  Note:
        *  1) Download the image datasets from imagenet for a number of different labels (For eg: dogs, cats, bears etc..).
        *     URL -> http://www.image-net.org/
        *  2) Create different sub directories for the category of image files that was downloaded.
        *     The subdirectory folder names indicates the possible outcomes (labels) of the model.
        *
        *  This is a simple example to check whether your data is properly extracted from source.
        *
        * */
        try {
            FileSplit fileSplit = new FileSplit(new File("Path/to/image-files"),NativeImageLoader.ALLOWED_FORMATS,new Random(123));
            int numLabels = fileSplit.getRootDir().listFiles(File::isDirectory).length;

            ParentPathLabelGenerator parentPathLabelGenerator = new ParentPathLabelGenerator();
            BalancedPathFilter balancedPathFilter = new BalancedPathFilter(
                    new Random(123),
                    NativeImageLoader.ALLOWED_FORMATS,
                    parentPathLabelGenerator
            );

            InputSplit[] inputSplits = fileSplit.sample(balancedPathFilter,85,15);
            InputSplit trainData = inputSplits[0];
            //InputSplit testData = inputSplits[1];

            ImageRecordReader imageRecordReader =  new ImageRecordReader(30,30,3,
                    parentPathLabelGenerator
            );
            imageRecordReader.initialize(trainData,null);

            DataSetIterator dataSetIterator = new RecordReaderDataSetIterator(imageRecordReader,4,1,numLabels);
            System.out.println(dataSetIterator.totalOutcomes());
        } catch(IllegalArgumentException e){
            System.out.println("Please provide proper image directory path in place of: Path/to/image-files ");
            System.out.println("For more details, please refer to the instructions listed in comment section");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
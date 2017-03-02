import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * <p>
 * You must add code for the 1 member and 4 methods specified below.
 * <p>
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl {
    private DecTreeNode root;
    //ordered list of attributes
    private List<String> mTrainAttributes;
    //
    private ArrayList<ArrayList<Double>> mTrainDataSet;
    //Min number of instances per leaf.
    private int minLeafNumber = 10;

    /**
     * Answers static questions about decision trees.
     */
    DecisionTreeImpl() {
        // no code necessary this is void purposefully
    }

    /**
     * Build a decision tree given a training set then prune it using a tuning set.
     *
     * @param train: the training set
     * @param tune:  the tuning set
     */
    DecisionTreeImpl(ArrayList<ArrayList<Double>> trainDataSet, ArrayList<String> trainAttributeNames, int minLeafNumber) {
        this.mTrainAttributes = trainAttributeNames;
        this.mTrainDataSet = trainDataSet;
        this.minLeafNumber = minLeafNumber;
        this.root = buildTree(this.mTrainDataSet);
    }

    private DecTreeNode buildTree(ArrayList<ArrayList<Double>> dataSet) {
        // TODO: add code here
        Double best_gain = -1;
        String best_attr = "";
        Double best_thresh = -1;

        for (int i = 0; i < dataSet.get(0).size; i++) {
            //Creates a comparator allowing me to sort the dataSet by the ith attribute of each entry
            Comparator<ArrayList<Double>> myComparator = new Comparator<ArrayList<Double>>() {
                //private ArrayList one;
                @Override
                public int compare(ArrayList<Double> one, ArrayList<Double> two) {
                    if (one.get(i) == two.get(i))
                        return one.get(one.size - 1).compareTo(two.get(two.get(two.size - 1)));
                    else
                        return one.get(i).compareTo(two.get(i));
                }
            };

            DataBinder data_binder = new DataBinder(i, dataSet.get(0));
            //sorting the dataSet using the comparator
            Collections.sort(dataSet, myComparator);
            //Finding a threshold for dividing based on sorted dataSet
            ArrayList<Double> thresh_candidates = new ArrayList<Double>();
            int last_class = -1;
            //Looks for repeat classes and adds the appropriate threshold to a list of possible thresholds
            for (ArrayList<Double> element : dataSet) {
                if (element.get(element.size - 1) == last_class)
                    thresh_candidates.add((element.get(i) + dataSet.get(i)) / 2);
                else if (last_class == -1)
                    last_class = element.get(i);
            }
            /* Iterates through the list of possible thresholds and calculates their info gain, updating best_gain as
             * fitting.
             */
            int left_size  = 0;
            int right_size = 0;
            Double gain = 0;
            for (Double thresh_cand : thresh_candidates) {
                //Calculates the info gain
                for (ArrayList<Double> instance : dataSet) {
                    if (instance.get(i) <= thresh_cand)
                        left_size++;
                    else
                        right_size++;
                }
                gain = infoGain(left_size , right_size);
                if (gain >= best_gain) {
                    best_gain = infoGain(thresh_cand);
                    best_attr = this.mTrainAttributes.get(i);
                    best_thresh = thresh_cand;
                }
            }
        }
        this.attribute = best_attr;
        //Split the dataSet at the threshhold
        ArrayList<ArrayList<Double>> left_data = new ArrayList();
        ArrayList<ArrayList<Double>> right_data = new ArrayList();
        for (ArrayList<Double> instance : dataSet) {
            if (instance.get(mTrainAttributes.indexOf(best_attr)) <= best_thresh)
                left_data.add(instance);
            else
                right_data.add(instance);
        }
        //Set the fields of the node
        this.threshold = best_thresh;
        this.attribute = best_attr;
        //Recursively build subtrees
        if(left_data.isEmpty() && right_data.isEmpty()) {
            return null;
        }
        if (!left_data.isEmpty())
            current.left = buildTree(left_data);
        if (!right_data.isEmpty())
            current.right = buildTree(right_data);

    }

    private Double infoGain(int left_size, int right_size) {
        return (Math.log(left_size)/Math.log(2)) + (Math.log(right_size)/Math.log(2));
    }

    public int classify(List<Double> instance) {
        DecTreeNode current = this.root;
        while (!current.isLeaf()) {
            int attr = instance.get(mTrainAttributes.indexOf(current.attribute));
            if (attr <= current.thresh && current.left != null)
                current = current.left;
            else if (current.right != null)
                current = current.right;
        }
        return current.classLabel;
    }

    public void rootInfoGain(ArrayList<ArrayList<Double>> dataSet, ArrayList<String> trainAttributeNames, int minLeafNumber) {
        this.mTrainAttributes = trainAttributeNames;
        this.mTrainDataSet = dataSet;
        this.minLeafNumber = minLeafNumber;
        // TODO: add code here

        //TODO: modify this example print statement to work with your code to output attribute names and info gain. Note the %.6f output format.
        for (int i = 0; i < bestSplitPointList.length; i++) {
            System.out.println(this.mTrainAttributes.get(i) + " " + String.format("%.6f", bestSplitPointList[i][0]));
        }
    }


    /**
     * Print the decision tree in the specified format
     */
    public void print() {
        printTreeNode("", this.root);
    }

    /**
     * Recursively prints the tree structure, left subtree first, then right subtree.
     */
    public void printTreeNode(String prefixStr, DecTreeNode node) {
        String printStr = prefixStr + node.attribute;

        System.out.print(printStr + " <= " + String.format("%.6f", node.threshold));
        if (node.left.isLeaf()) {
            System.out.println(": " + String.valueOf(node.left.classLabel));
        } else {
            System.out.println();
            printTreeNode(prefixStr + "|\t", node.left);
        }
        System.out.print(printStr + " > " + String.format("%.6f", node.threshold));
        if (node.right.isLeaf()) {
            System.out.println(": " + String.valueOf(node.right.classLabel));
        } else {
            System.out.println();
            printTreeNode(prefixStr + "|\t", node.right);
        }
    }

    public Double printAccuracy(int numEqual, int numTotal) {
        Double accuracy = numEqual / (Double) numTotal;
        System.out.println(accuracy);
        return accuracy;
    }

    /**
     * Private class to facilitate instance sorting by argument position since java doesn't like passing variables to comparators through
     * nested variable scopes.
     */
    private class DataBinder {

        public ArrayList<Double> mData;
        public int i;

        public DataBinder(int i, ArrayList<Double> mData) {
            this.mData = mData;
            this.i = i;
        }

        public Double getArgItem() {
            return mData.get(i);
        }

        public ArrayList<Double> getData() {
            return mData;
        }

    }

}
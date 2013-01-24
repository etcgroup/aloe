package etc.aloe.wt2013;

import java.util.*;
import weka.classifiers.*;
import weka.classifiers.bayes.HMM;
import weka.core.*;

public class HmmTest {

    public static final double probabilityOfError = 0.5;
    static Classifier HMM;
    static String file;
    static Instances data;

    public HmmTest() {
        super();
    }

    public static void main(String args[]) throws Exception {

        Instances train = sample(Arrays.asList("false", "true"), 2, 500, 5);
        Instances test1 = sample(Arrays.asList("false", "true"), 2, 500, 5);

        HMM hmm = new HMM();

        hmm.buildClassifier(train);
        Evaluation eval = new Evaluation(train);

        eval.evaluateModel(hmm, train);
        double errorRate = eval.errorRate();
        System.out.println("Test Seq 3_2 error rate " + errorRate);
        assert (errorRate < 0.25);

        eval.evaluateModel(hmm, test1);
        errorRate = eval.errorRate();
        System.out.println("Test Seq 3_2 error rate " + errorRate);
        assert (errorRate < 0.25);

    }

    public static Instances sample(List<String> classNames, int numOutputs, int numseqs, int length) throws Exception {

        ArrayList<Attribute> attrs = new ArrayList<Attribute>();
        ArrayList<String> seqIds = new ArrayList<String>();
        for (int i = 0; i < numseqs; i++) {
            seqIds.add("seq_" + i);
        }
        attrs.add(new Attribute("seq-id", seqIds));
        attrs.add(new Attribute("class", classNames));


        ArrayList<Attribute> seqAttrs = new ArrayList<Attribute>();
        ArrayList<String> outputs = new ArrayList<String>();
        for (int i = 0; i < numOutputs; i++) {
            outputs.add("output_" + i);
        }

        seqAttrs.add(new Attribute("output", outputs));
        Instances seqHeader = new Instances("seq", seqAttrs, 0);

        attrs.add(new Attribute("sequence", seqHeader));
        Instances seqs = new Instances("test", attrs, numseqs);
        seqs.setClassIndex(1);

        for (int i = 0; i < numseqs; i++) {
            Random r = new Random();

            seqs.add(new DenseInstance(3));
            Instance inst = seqs.lastInstance();
            inst.setValue(0, seqIds.get(i));
            int classId = r.nextInt(classNames.size());
            inst.setValue(1, classNames.get(classId));

            //HMMEstimator model = models.get(classId);
            Instances sequence = new Instances(seqIds.get(i), seqAttrs, length);
            //hmmValue current = new hmmValue();

            sequence.add(new DenseInstance(1));
            int currentOutput = r.nextInt(outputs.size());
            sequence.lastInstance().setValue(0, outputs.get(currentOutput));

            for (int a = 1; a < length; a++) {
                sequence.add(new DenseInstance(1));
                if (currentOutput == classId) {
                    if (r.nextInt((int) (1 / probabilityOfError)) != 0) {
                        sequence.lastInstance().setValue(0, outputs.get(currentOutput));
                    } else {
                        int randomNextOutput = r.nextInt(outputs.size());
                        sequence.lastInstance().setValue(0, outputs.get(randomNextOutput));
                    }
                } else {
                    int randomNextOutput = r.nextInt(outputs.size());
                    currentOutput = randomNextOutput;
                    sequence.lastInstance().setValue(0, outputs.get(randomNextOutput));
                }
            }

            inst.setValue(seqs.attribute(2), seqs.attribute(2).addRelation(sequence));
        }
        return seqs;
    }
}
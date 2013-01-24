package etc.aloe.filters;

import java.util.*;
import weka.core.*;
import weka.filters.*;

public class AddRelationalAttributeFilter extends SimpleBatchFilter {
    
    List<String> classNames = Arrays.asList("true", "false");

    @Override
    public String globalInfo() {
        return "A simple batch filter that adds a relational attribute to the data.";
    }

    @Override
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.enableAllAttributes();
        result.enableAllClasses();
        return result;
    }

    @Override
    protected Instances determineOutputFormat(Instances inputFormat) {
        
        ArrayList<Attribute> resultAttrs = new ArrayList<Attribute>();
        resultAttrs.add(new Attribute("seq-id"));
        resultAttrs.add(new Attribute("class", classNames));
        Instances seqHeader = new Instances(inputFormat, 0);
        resultAttrs.add(new Attribute("sequence", seqHeader));
        Instances returnable = new Instances("format", resultAttrs, 0);
        returnable.setClassIndex(1);
        
        return returnable;
    }
    
    @Override
    /**
   * Signify that this batch of input to the filter is finished. If
   * the filter requires all instances prior to filtering, output()
   * may now be called to retrieve the filtered instances. Any
   * subsequent instances filtered should be filtered based on setting
   * obtained from the first batch (unless the setInputFormat has been
   * re-assigned or new options have been set). Sets m_FirstBatchDone
   * and m_NewBatch to true.
   *
   * @return 		true if there are instances pending output
   * @throws IllegalStateException 	if no input format has been set. 
   * @throws Exception	if something goes wrong
   * @see    		#m_NewBatch
   * @see    		#m_FirstBatchDone 
   */
  public boolean batchFinished() throws Exception {
    int         i;
    Instances   inst;
    
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined");

    // get data
    inst = new Instances(getInputFormat());

    // if output format hasn't been set yet, do it now
    if (!hasImmediateOutputFormat() && !isFirstBatchDone())
      setOutputFormat(determineOutputFormat(new Instances(inst, 0)));

    // don't do anything in case there are no instances pending.
    // in case of second batch, they may have already been processed
    // directly by the input method and added to the output queue
    if (inst.numInstances() > 0) {
      // process data
      inst = process(inst);

      // clear input queue
      flushInput();

      // move it to the output
      for (i = 0; i < inst.numInstances(); i++)
	push(inst.instance(i));
    }
    
    m_NewBatch       = true;
    //m_FirstBatchDone = true;
    
    return (numPendingOutput() != 0);
  }
    

    @Override
    protected Instances process(Instances inst) {
        return addRelationalAttribute(inst, 2, inst.size()/4, 2);
    }

    public Instances addRelationalAttribute(Instances base, int numOutputs, int numseqs, int length) {

        Instances result = new Instances(determineOutputFormat(base), 0);
        
        result.setClassIndex(1);

        for (int i = 0; i < numseqs; i++) {

            result.add(new DenseInstance(3));
            Instance resultInstance = result.lastInstance();
            resultInstance.setValue(0, i);

            Instances sequence = new Instances(base, length);

            for (int a = 0; a < length; a++) {
                Instance toBeAddedInstance = base.get((i * length) + a );
                sequence.add(toBeAddedInstance);
                if (a == length - 1){
                    resultInstance.setClassValue(toBeAddedInstance.classValue());
                }
            }
            resultInstance.setValue(result.attribute(2), result.attribute(2).addRelation(sequence));
        }

        return result;
    }

    public static void main(String[] args) {
        runFilter(new AddRelationalAttributeFilter(), args);
    }
}
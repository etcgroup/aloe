package etc.aloe.data;

import etc.aloe.processes.Saving;
import java.io.File;
import java.io.IOException;

/**
 * The EvaluationReport contains data about model performance as compared to
 * a source of truth data.
 */
public class EvaluationReport implements Saving {

    @Override
    public boolean save(File destination) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addPartial(EvaluationReport report) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}

package etc.aloe.data;

import com.csvreader.CsvReader;
import etc.aloe.processes.Loading;
import etc.aloe.processes.Saving;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * MessageSet contains messages.
 */
public class MessageSet implements Loading, Saving {

    private List<Message> messages = new ArrayList<Message>();
    private static final int ID_COLUMN = 0;
    private static final int TIME_COLUMN = 1;
    private static final int PARTICIPANT_COLUMN = 2;
    private static final int MESSAGE_COLUMN = 3;
    private static final int TRUTH_COLUMN = 4;
    private static final int PREDICTION_COLUMN = 5;
    private static final int SEGMENT_COLUMN = 6;
    private static final String ID_COLUMN_NAME = "id";
    private static final String TIME_COLUMN_NAME = "time";
    private static final String PARTICIPANT_COLUMN_NAME = "participant";
    private static final String MESSAGE_COLUMN_NAME = "message";
    private static final String TRUTH_COLUMN_NAME = "truth";
    private static final String PREDICTION_COLUMN_NAME = "predicted";
    private static final String SEGMENT_COLUMN_NAME = "segment";
    private DateFormat dateFormat;

    public void add(Message message) {
        messages.add(message);
    }

    public List<Message> getMessages() {
        return messages;
    }

    private void validateCSVHeaders(CsvReader csvReader) throws InvalidObjectException {

        String[] headers = null;
        try {
            if (!csvReader.readHeaders()) {
                throw new InvalidObjectException("CSV must contain headers in the first row");
            }
            headers = csvReader.getHeaders();
        } catch (IOException e) {
            throw new InvalidObjectException(e.getMessage());
        }

        if (headers.length <= MESSAGE_COLUMN) {
            throw new InvalidObjectException("CSV must contain at least " + (MESSAGE_COLUMN + 1) + " columns");
        }

        if (headers.length > TRUTH_COLUMN + 1) {
            throw new InvalidObjectException("CSV must contain no more than " + (TRUTH_COLUMN + 1) + " columns");
        }

        if (!ID_COLUMN_NAME.equals(headers[ID_COLUMN])) {
            throw new InvalidObjectException("Column " + ID_COLUMN + " must be '" + ID_COLUMN_NAME + "'");
        }

        if (!TIME_COLUMN_NAME.equals(headers[TIME_COLUMN])) {
            throw new InvalidObjectException("Column " + TIME_COLUMN + " must be '" + TIME_COLUMN_NAME + "'");
        }

        if (!PARTICIPANT_COLUMN_NAME.equals(headers[PARTICIPANT_COLUMN])) {
            throw new InvalidObjectException("Column " + PARTICIPANT_COLUMN + " must be '" + PARTICIPANT_COLUMN_NAME + "'");
        }

        if (!MESSAGE_COLUMN_NAME.equals(headers[MESSAGE_COLUMN])) {
            throw new InvalidObjectException("Column " + MESSAGE_COLUMN + " must be '" + MESSAGE_COLUMN_NAME + "'");
        }

        if (headers.length > TRUTH_COLUMN) {
            if (!TRUTH_COLUMN_NAME.equals(headers[TRUTH_COLUMN])) {
                throw new InvalidObjectException("Column " + TRUTH_COLUMN + " must be '" + TRUTH_COLUMN_NAME + "'");
            }
        }
    }

    @Override
    public boolean load(File source) throws FileNotFoundException, InvalidObjectException {
        CsvReader csvReader = new CsvReader(new FileInputStream(source), Charset.forName("UTF-8"));

        try {
            validateCSVHeaders(csvReader);

            int lineNumber = 1;
            while (csvReader.readRecord()) {
                lineNumber++;

                String idText = csvReader.get(ID_COLUMN);
                String messageText = csvReader.get(MESSAGE_COLUMN);
                String participant = csvReader.get(PARTICIPANT_COLUMN);
                String timeText = csvReader.get(TIME_COLUMN);
                String truthText = csvReader.get(TRUTH_COLUMN).toLowerCase();

                int id = -1;
                try {
                    Integer.parseInt(idText);
                } catch (NumberFormatException e) {
                    throw new InvalidObjectException("Invalid value '" + idText + "' for '" + ID_COLUMN_NAME + "' on line " + lineNumber);
                }

                Date time = null;
                try {
                    time = getDateFormat().parse(timeText);
                } catch (ParseException e) {
                    throw new InvalidObjectException("Invalid value '" + timeText + "' for '" + TIME_COLUMN_NAME + "' on line " + lineNumber);
                }

                Boolean truth = null;
                if (truthText.equals("1") || truthText.equals("true")) {
                    truth = true;
                } else if (truthText.equals("0") || truthText.equals("false")) {
                    truth = false;
                } else if (truthText.equals("")) {
                    truth = null;
                } else {
                    throw new InvalidObjectException("Invalid value '" + truthText + "' for '" + TRUTH_COLUMN_NAME + "' on line " + lineNumber);
                }

                Message message = new Message(id, time, participant, messageText, truth);
                this.add(message);
            }

            System.out.println("Loaded " + this.size() + " messages into message set.");

        } catch (IOException ex) {
            throw new InvalidObjectException(ex.getMessage());
        }

        return true;
    }

    @Override
    public boolean save(File destination) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    private int size() {
        return this.messages.size();
    }
}

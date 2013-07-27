/*
 * This file is part of ALOE.
 *
 * ALOE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * ALOE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with ALOE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2012 SCCL, University of Washington (http://depts.washington.edu/sccl)
 */
package etc.aloe.data;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import etc.aloe.processes.Loading;
import etc.aloe.processes.Saving;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * MessageSet contains messages.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
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
    private static final int MIN_INPUT_COLUMNS = 4;
    private static final int NUM_OUTPUT_COLUMNS = 7;
    private static final String ID_COLUMN_NAME = "id";
    private static final String TIME_COLUMN_NAME = "time";
    private static final String PARTICIPANT_COLUMN_NAME = "participant";
    private static final String MESSAGE_COLUMN_NAME = "message";
    private static final String TRUTH_COLUMN_NAME = "truth";
    private static final String PREDICTION_COLUMN_NAME = "predicted";
    private static final String SEGMENT_COLUMN_NAME = "segment";
    private DateFormat dateFormat;
    private Charset charset = Charset.forName("UTF-8");

    /**
     * Add a message to the set.
     *
     * @param message
     */
    public void add(Message message) {
        messages.add(message);
    }

    /**
     * Get the underlying list of messages.
     *
     * @return
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Makes sure that the csv headers contain the minimum required fields.
     *
     * @param csvReader
     * @throws InvalidObjectException
     */
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

        if (headers.length < MIN_INPUT_COLUMNS) {
            throw new InvalidObjectException("CSV must contain at least " + (MIN_INPUT_COLUMNS) + " columns");
        }

        if (headers.length > NUM_OUTPUT_COLUMNS) {
            throw new InvalidObjectException("CSV must contain no more than " + (NUM_OUTPUT_COLUMNS) + " columns");
        }

        List<String> headerList = Arrays.asList(headers);
        if (!headerList.contains(ID_COLUMN_NAME)) {
            throw new InvalidObjectException("'" + ID_COLUMN_NAME + "' column must be present.");
        }

        if (!headerList.contains(TIME_COLUMN_NAME)) {
            throw new InvalidObjectException("'" + TIME_COLUMN_NAME + "' column must be present.");
        }

        if (!headerList.contains(PARTICIPANT_COLUMN_NAME)) {
            throw new InvalidObjectException("'" + PARTICIPANT_COLUMN_NAME + "' column must be present.");
        }

        if (!headerList.contains(MESSAGE_COLUMN_NAME)) {
            throw new InvalidObjectException("'" + MESSAGE_COLUMN_NAME + "' column must be present.");
        }
    }

    @Override
    public boolean load(InputStream source) throws InvalidObjectException {
        if (dateFormat == null) {
            throw new IllegalStateException("No date format provided.");
        }

        CsvReader csvReader = new CsvReader(source, charset);

        //Start recording labels
        Label.startLabelSet();

        try {
            validateCSVHeaders(csvReader);

            int lineNumber = 1;
            int numLabeled = 0;
            while (csvReader.readRecord()) {
                lineNumber++;

                String idText = csvReader.get(ID_COLUMN_NAME);
                String messageText = csvReader.get(MESSAGE_COLUMN_NAME);
                String participant = csvReader.get(PARTICIPANT_COLUMN_NAME);
                String timeText = csvReader.get(TIME_COLUMN_NAME);
                String truthText = csvReader.get(TRUTH_COLUMN_NAME).toLowerCase();
                String predictionText = csvReader.get(PREDICTION_COLUMN_NAME).toLowerCase();
                String segmentIdText = csvReader.get(SEGMENT_COLUMN_NAME).toLowerCase();

                int id = -1;
                try {
                    id = Integer.parseInt(idText);
                } catch (NumberFormatException e) {
                    throw new InvalidObjectException("Invalid value '" + idText + "' for '" + ID_COLUMN_NAME + "' on line " + lineNumber);
                }

                Date time = null;
                try {
                    time = getDateFormat().parse(timeText);
                } catch (ParseException e) {
                    throw new InvalidObjectException("Invalid value '" + timeText + "' for '" + TIME_COLUMN_NAME + "' on line " + lineNumber);
                }

                Label truth = null;
                if (!truthText.trim().isEmpty()) {
                    truth = Label.get(truthText.trim());
                    numLabeled++;
                }

                Label prediction = null;
                if (!predictionText.trim().isEmpty()) {
                    prediction = Label.get(predictionText.trim());
                }

                int segment = -1;
                if (segmentIdText.equals("")) {
                    segment = -1;
                } else {
                    try {
                        segment = Integer.parseInt(segmentIdText);
                    } catch (NumberFormatException e) {
                        throw new InvalidObjectException("Invalid value '" + segmentIdText + "' for '" + SEGMENT_COLUMN_NAME + "' on line " + lineNumber);
                    }
                }

                Message message = new Message(id, time, participant, messageText, truth, prediction, segment);
                this.add(message);
            }

            System.out.println("Loaded " + this.size() + " raw messages (" + numLabeled + " labeled).");

        } catch (IOException ex) {
            throw new InvalidObjectException(ex.getMessage());
        }

        //No more new labels can be registered now
        Label.closeLabelSet();

        return true;
    }

    @Override
    public boolean save(OutputStream destination) throws IOException {
        if (dateFormat == null) {
            throw new IllegalStateException("No date format provided.");
        }

        CsvWriter out = new CsvWriter(destination, ',', charset);

        String[] row = new String[NUM_OUTPUT_COLUMNS];
        row[ID_COLUMN] = ID_COLUMN_NAME;
        row[PARTICIPANT_COLUMN] = PARTICIPANT_COLUMN_NAME;
        row[TIME_COLUMN] = TIME_COLUMN_NAME;
        row[MESSAGE_COLUMN] = MESSAGE_COLUMN_NAME;
        row[TRUTH_COLUMN] = TRUTH_COLUMN_NAME;
        row[PREDICTION_COLUMN] = PREDICTION_COLUMN_NAME;
        row[SEGMENT_COLUMN] = SEGMENT_COLUMN_NAME;

        out.writeRecord(row);

        for (Message message : messages) {
            row[ID_COLUMN] = Integer.toString(message.getId());
            row[PARTICIPANT_COLUMN] = message.getParticipant();
            row[TIME_COLUMN] = dateFormat.format(message.getTimestamp());
            row[MESSAGE_COLUMN] = message.getMessage();
            row[TRUTH_COLUMN] = message.hasTrueLabel() ? message.getTrueLabel().toString() : null;
            row[PREDICTION_COLUMN] = message.hasPredictedLabel() ? message.getPredictedLabel().toString() : null;
            row[SEGMENT_COLUMN] = message.hasSegmentId() ? Integer.toString(message.getSegmentId()) : null;

            out.writeRecord(row);
        }

        out.flush();
        return true;
    }

    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    /**
     * Set the date format used to import/export timestamps.
     *
     * @param dateFormat
     */
    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * Get the size of the message set.
     *
     * @return
     */
    public int size() {
        return this.messages.size();
    }

    /**
     * Get the ith message.
     *
     * @param i
     * @return
     */
    public Message get(int i) {
        return this.messages.get(i);
    }
}

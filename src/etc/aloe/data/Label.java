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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a label for a message/segment.
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class Label {

    private final String name;
    private final int number;
    private static List<Label> values = new ArrayList<Label>();

    private Label(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Label get(String name) {
        for (Label label : values) {
            if (label.getName().equals(name)) {
                return label;
            }
        }

        Label label = new Label(name, values.size());
        values.add(label);
        return label;
    }

    public static Label get(int number) {
        if (number >= 0 && number < values.size()) {
            return values.get(number);
        }

        Label label = new Label(number + "", values.size());
        values.add(label);
        return label;
    }

    public static boolean exists(String name) {
        for (Label label : values) {
            if (label.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean exists(int number) {
        if (number >= 0 && number < values.size()) {
            return true;
        }

        return false;
    }

    public static int getLabelCount() {
        return values.size();
    }

    public static List<String> getNameList() {
        List<String> list = new ArrayList<String>();
        for (Label label : values) {
            list.add(label.getName());
        }
        return list;
    }
}

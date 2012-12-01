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
package etc.aloe.cscw2013;

import etc.aloe.data.ExampleSet;
import etc.aloe.data.FeatureSpecification;
import etc.aloe.data.Message;
import etc.aloe.data.Segment;
import etc.aloe.data.SegmentSet;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import weka.filters.Filter;

/**
 *
 * @author Michael Brooks <mjbrooks@uw.edu>
 */
public class FeatureGenerationImplTest {

    private List<String> termList;
    private SegmentSet segments;
    private ExampleSet basicExamples;

    public FeatureGenerationImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        termList = Arrays.asList(new String[]{
                    ":)",
                    ":-)",
                    ":-(",
                    ":>",
                    ";)",
                    ";-)"
                });

        segments = new SegmentSet();

        Segment seg0 = new Segment();
        seg0.add(new Message(0, new Date(), "Alice", "it's"));
        seg0.add(new Message(1, new Date(), "Bob", "cow"));
        seg0.add(new Message(2, new Date(), "Alice", "time"));
        seg0.setTrueLabel(Boolean.TRUE);
        segments.add(seg0);

        Segment seg1 = new Segment();
        seg1.add(new Message(3, new Date(), "Bob", "noooooooo"));
        seg1.setTrueLabel(Boolean.FALSE);
        segments.add(seg1);

        Segment seg2 = new Segment();
        seg2.add(new Message(4, new Date(), "Bob", "once"));
        seg2.add(new Message(5, new Date(), "Alice", "upon"));
        seg2.setTrueLabel(Boolean.FALSE);
        segments.add(seg2);

        basicExamples = segments.getBasicExamples();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of generateFeatures method, of class FeatureGenerationImpl.
     */
    @Test
    public void testGenerateFeatures() {
        System.out.println("generateFeatures");

        FeatureGenerationImpl generation = new FeatureGenerationImpl(termList);
        FeatureSpecification spec = generation.generateFeatures(basicExamples);

        assertNotNull(spec);
        assertTrue(spec.getFilters().size() > 0);

        for (Filter filter : spec.getFilters()) {
            assertTrue(filter.isOutputFormatDefined());
            assertTrue(filter.isFirstBatchDone());

            try {
                basicExamples.setInstances(Filter.useFilter(basicExamples.getInstances(), filter));
            } catch (Exception e) {
                assertTrue(e.getMessage(), false);
            }
        }
    }
}

```
Version: v1.0
Revision: b913bc18cd0a25c95a15f45ad41b0eb3f699b4f1
Build Time: 2013-10-25T14:10:06.449-0700
```
ALOE
====

ALOE stands for Affect Labeler of Expressions. The latest version is 1.0.

ALOE was developed to train and test machine learning classifiers for
automatically labeling chat messages with different emotion or affect categories.
The software runs in one of four modes:

* In "train" mode, ALOE takes a list of messages with ground truth labels
(either "true" or "false") and trains a classifier to predict the labels
for unseen messages.

* In "label" mode, ALOE uses a classifier it has already trained, to
generate predicted labels for a set of unlabeled messages, or to evaluate
the classifier on a labeled "test" set.

* In "single" mode, ALOE uses a previous classifier to label a single message
provided as a command line argument ("-x" option).

* Finally, ALOE features an "interactive" mode where a trained model is used
to predict the label for messages that you type while the program runs.

These three modes are explained in detail below.

**[Download ALOE 1.0 (Fall 2012)](https://github.com/downloads/etcgroup/aloe/aloe-v1.0.zip)** -- 
**[View Javadocs](http://etcgroup.github.io/aloe/javadoc-v1.0/)**

**[Download ALOE 1.1 (Fall 2013)](https://github.com/etcgroup/aloe/releases/tag/1.1)** -- 
**[View Javadocs](http://etcgroup.github.io/aloe/javadoc-v1.1/)**


## Running ALOE

The basic usage for ALOE is the following:

```
java -jar aloe.jar PIPELINE_CLASS MODE OPTIONS...
```

The `PIPELINE_CLASS` must be the name of a class in the `etc.aloe.factories` package,
which implements `etc.aloe.factories.PipelineFactory`. Currently, the only
available class is `CSCW2013`.

The `MODE` can be one of "train", "label", "single", or "interactive". Each mode has
its own required and optional arguments, detailed below.

The following are some common options that can be used in any of the three modes:

* `--dateformat DATE_FORMAT`, `-d DATE_FORMAT`: Provide a custom
  date format string (default is 'yyyy-MM-dd HH:mm:ss') as for
  [SimpleDateFormat](http://download.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html).

* `--random N`, `-r N`: Random seed for the Random instance shared across ALOE.

### Pipeline Classes

Specific pipeline classes define additional options, depending on the mode.
The available pipelines are listed here. Basic options available (or required)
regardless of the pipeline selected are in the following sections.

* [CSCW2013](http://github.com/etcgroup/aloe/blob/master/docs/cscw2013.md) - segmentation by time threshold, mixed feature set, and linear SVM classification.

### Data Format

The data files consumed or produced by ALOE are in [comma-separated value format](http://creativyst.com/Doc/Articles/CSV/CSV01.htm).

Input files must minimally have `id`, `time`, `participant`, and `message` columns.

* `id`: a positive integer uniquely identifying the message.
* `time`: a value parseable by [SimpleDateFormat](download.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html).
  The default date-time format is that of the MySQL DATETIME type: 'yyyy-MM-dd HH:mm:ss' but other
  formats can be provided via the `--dateformat` option.
* `participant`: contains the name of the person who originated each message. If that isn't
   relevant for your data set, you can just use the same participant name (or an empty string) for each record.
* `message`: contains the actual text of the message.

A `truth` column may be provided if the data is labeled. Its values should be `true`, `false`, or empty.

Output CSV files will have a similar format, but with added `predicted` and `segment` columns.
The `predicted` column indicates the predicted label for the message, `true` or `false`. The
`segment` column contains an integer id of the segment to which the message was assigned.

Sample messages with ground truth labels (i.e. data for "train" mode):
```
id,time,participant,message,truth
5,2004-11-27 03:36:32,Alice,Hello,false
9,2004-11-27 03:36:43,Bob,Well hello there.,true
```

Sample messages with partial labeling (i.e. data for "test" mode):
```
id,time,participant,message,truth
5,2004-11-27 03:36:32,Alice,Hello,false,false
9,2004-11-27 03:36:43,Bob,Well hello there.,true
10,2004-11-27 03:36:49,Alice,I am super happy today!,
11,2004-11-27 03:37:01,Bob,"Why, there's nothing to be happy about!",
12,2004-11-27 03:37:15,Bob,In fact I am going back to bed.,
```

Sample labeled output:
```
id,time,participant,message,truth,predicted,segment
5,2004-11-27 03:36:32,Alice,Hello,false,false,1
9,2004-11-27 03:36:43,Bob,Well hello there.,true,true,2
10,2004-11-27 03:36:49,Alice,I am super happy today!,,true,1
11,2004-11-27 03:37:01,Bob,"Why, there's nothing to be happy about!",,true,2
12,2004-11-27 03:37:15,Bob,In fact I am going back to bed.,,false,2
```

### Train Mode

In "train" mode, ALOE performs the following tasks:
1. Read labeled data from a CSV file.
2. Segment the data so that closely related messages are considered together.
3. Use cross validation to evaluate the features/classifier on the segmented data.
4. Train an overall classifier on the segmented data.
5. Produce output including the trained model, feature specification, and evaluation.

#### Usage

```
java -jar aloe.jar PIPELINE_CLASS train INPUT_CSV OUTPUT_DIR [options...]
```

`INPUT_CSV` is a required path to a comma-separated value (CSV) file with labeled message data (format described above).
The input file must minimally contain columns for `id`, `participant`, `time`, `message`, and `truth`.

`OUTPUT_DIR` is a required path to a directory where ALOE's output files will be created. The output files that ALOE produces
in "train" mode are described below. **Files in this directory may be overwritten.**

Optional: Use the `--roc` option to generate a ROC curve for each fold of cross validation.
You can also use the `--feature-values` flag to output "feature_values.csv", a dump of the training data
after feature extraction.

#### Output

Within the provided `OUTPUT_DIR`, ALOE will create the following files:

* *features.spec*: A binary file containing the fully configured filters used to extract features
   from the message data.
* *model.model*: A binary file containing the trained model. This file should always be paired with
   its matching *features.spec* file for future use.
* *report.txt*: A human-readable report about cross-validation results.
* *top_features.txt*: A human-readable ranked list of the top 10 most highly-weighted features.
* *feature_weights.csv*: A CSV spreadsheet listing the weight that was assigned to each feature
   by the classifier.
* *feature_values.csv*: A CSV spreadsheet with the features extracted from every training instance, if the `--feature-values` flag was used.
* *rocs/Fold N.csv*: If `--roc` was used, CSV spreadsheets containing the ROC curves for each fold of cross validation.

**Files in the output directory may be overwritten.**

### Label Mode

In "label" mode, ALOE performs the following steps:
1. Read and segment message data from a CSV file.
2. Read a trained model and feature specification from files.
3. Use the model to classify the segments.
4. If the input data contained any labeled messages, evaluate the predicted labels against the provided labels.
5. Save output including the now-labeled messages and the evaluation report.

#### Usage

```
java -jar aloe.jar PIPELINE_CLASS label INPUT_CSV OUTPUT_DIR -m MODEL_FILE -f FEATURES_FILE [options...]
```

`INPUT_CSV` is a required path to a comma-separated value (CSV) file with message data (format described above).
The input file must minimally contain columns for `id`, `participant`, `time`, and `message`. If a `truth` column
is provided, messages for which a truth value is provided will be labeled and used to evaluate the classifier.

`OUTPUT_DIR` is a required path to a directory where ALOE's output files will be created. The output files that ALOE produces
in "label" mode are described below. **Files in this directory may be overwritten.**

Required options:
* `--features FEATURES_FILE`, `-f FEATURES_FILE`: Path to an existing feature specification file (i.e. *features.spec*),
   produced in "train" mode.
* `--model MODEL_FILE`, `-m MODEL_FILE`: Path to an existing model file (i.e. *model.model*),
   produced in "train" mode. **This must match the provided features file.**

Optional: Use the `--roc` option to generate a ROC curve from any data that was already labeled.
You can also use the `--feature-values` flag to output "feature_values.csv", a dump of the training data
after feature extraction.

#### Output

Within the provided `OUTPUT_DIR`, ALOE will create the following files:

* *report.txt*: A human-readable report about cross-validation results. This is only produced if some of the input
   data had ground-truth labels provided.
* *labeled.csv*: A CSV spreadsheet containing the input data, with new `predicted` and `segment` columns.
* *feature_values.csv*: A CSV spreadsheet with the features extracted from every training instance, if the `--feature-values` flag was used.
* *roc.csv*: A CSV spreadsheet containing the ROC curve, if `--roc` was used.

**Files in the output directory may be overwritten.**

### Single Mode

In "single" mode, ALOE performs the following steps:
1. Read a trained model and feature specification from files.
2. Label the message provided via the "-x" argument and classify using the loaded model.
3. Print the label.

#### Usage

```
java -jar aloe.jar PIPELINE_CLASS single -m MODEL_FILE -f FEATURES_FILE -x MESSAGE_TEXT [options...]
```

Required options:
* `--features FEATURES_FILE`, `-f FEATURES_FILE`: Path to an existing feature specification file (i.e. *features.spec*),
   produced in "train" mode.
* `--model MODEL_FILE`, `-m MODEL_FILE`: Path to an existing model file (i.e. *model.model*),
   produced in "train" mode. **This must match the provided features file.**
* `--message MESSAGE_TEXT`, `-x MESSAGE_TEXT`: Text of message to label.

#### Output

ALOE simply prints `true` if the model predicted that the label applies, and `false` otherwise.

### Interactive Mode

In "interactive" mode, ALOE performs the following steps:
1. Read a trained model and feature specification from files.
2. Repeatedly read messages from standard input and repeatedly classify each one using the loaded model.
3. Save the labeled messages.

#### Usage

```
java -jar aloe.jar PIPELINE_CLASS interactive OUTPUT_DIR -m MODEL_FILE -f FEATURES_FILE [options...]
```

`OUTPUT_DIR` is a required path to a directory where ALOE's output files will be created. The output files that ALOE produces
in "interactive" mode are described below. **Files in this directory may be overwritten.**

Required options:
* `--features FEATURES_FILE`, `-f FEATURES_FILE`: Path to an existing feature specification file (i.e. *features.spec*),
   produced in "train" mode.
* `--model MODEL_FILE`, `-m MODEL_FILE`: Path to an existing model file (i.e. *model.model*),
   produced in "train" mode. **This must match the provided features file.**

#### Output

Within the provided `OUTPUT_DIR`, ALOE will create the following file:

* *labeled.csv*: A CSV spreadsheet containing the messages typed by the user, with `predicted` column,
   indicating the predicted label for the message: `true` or `false`.

**Files in the output directory may be overwritten.**

## Building ALOE

ALOE is distributed as a project for the [NetBeans IDE](http://netbeans.org), so we recommend
using NetBeans if you want to build ALOE from source. You should also be able to use the included
Ant build file directly, if you want.

ALOE depends on several 3rd party jar files that you may need to obtain to build the project.
These can be extracted from the binary distribution, or you can download them yourself.
Your project directory should (minimally) have the following structure:

* build.xml
* manifest.mf
* nbproject/
* test/
* src/
* lib/
    * nblibraries.properties
    * [weka.jar](http://prdownloads.sourceforge.net/weka/weka-3-7-7.zip)
    * [javacsv.jar](http://sourceforge.net/projects/javacsv/files/JavaCsv/JavaCsv%202.1/javacsv2.1.zip)
    * [args4j-2.0.21.jar](http://maven.jenkins-ci.org/content/repositories/releases/args4j/args4j/2.0.21/args4j-2.0.21.jar)
    * junit_4/
        * junit-4.10.jar (included with NetBeans)
    * CopyLibs/
        * org-netbeans-modules-java-j2seproject-copylibstask.jar (included with NetBeans)

The `nblibraries.properties` file may also define links to source code for these libraries,
but this shouldn't be required to build.

You can build in NetBeans, or just run `ant`. This will produce a `dist` folder containing the main
aloe.jar file, all of the required libraries, and the javadocs.

## Acknowledgements

ALOE relies on machine learning implementations from the [Weka](http://www.cs.waikato.ac.nz/ml/weka/) data mining software.
Command line argument parsing is handled by [args4j](http://args4j.kohsuke.org/).
Parsing and writing of comma-separated value files uses [JavaCSV](http://www.csvreader.com/java_csv.php).

## Contributors

ALOE was created by members of the [Scientific Collabration and Creativity Lab](http://depts.washington.edu/sccl) at the [University of Washington](http://www.washington.edu).

Principle contributors:
* [Michael Brooks](http://students.washington.edu/mjbrooks)
* [Katie Kuksenok](http://students.washington.edu/kuksenok)

## Citing this software

If you publish research conducted using this software, please cite the following paper:

```
Brooks, M., Kuksenok, K., Torkildson, M. K., Perry, D., Robinson, J. J.,
  Scott, T. J., Anicello, O., Zukowski, A.,  Harris, P., Aragon, C. R. 2013.
  Statistical Affect Detection in Collaborative Chat. Proceedings of CSCW 2013. ACM.
```

More information is available on the [SCCL website](http://depts.washington.edu/sccl).

## Version History

* v1.0 - Initial release with implementations for our CSCW 2013 paper.

## License ##

ALOE is released under the GNU General Public License (version 3).

Copyright (c) 2012 SCCL, University of Washington (http://depts.washington.edu/sccl).

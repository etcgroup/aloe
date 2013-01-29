Profiling Scripts  
====
Note that both scripts are written in Python 3; profiler.py requires Python 3.3 or above.

##profiler.py  
The profiler.py script is used to run ALOE over a set of input files. As the script iterates, it incrementally parses each ALOE report file and appends the numeric results to an output csv file. Each row of the output file is prepended with an entry of the form `[affect code]_[pipeline name]`. The script attempts to parse the affect code from the filename - this assumes that the filename is of the form `export_[index]_[affect code].csv`. If the affect code cannot be parsed from the input CSV filename, the script will default to the filename itself. At this point in time it is up to the user to supply the proper column headings.

To use the profiler script, run it as such:  

```
python3 profiler.py -aloe [aloe directory] -in [input CSV file directory] -out [output directory] OPTIONS
```  
The `-h` flag may be used to print a brief summary of all arguments. These arguments are explained below:  

Directories:  
* `-aloe`, `--aloe-dir`: The top-level ALOE directory (not the path to aloe.jar). The script runs ALOE from this directory to avoid issues such as sourcing the emoticons file. It's assumed that the aloe jar is located inside `dist/`.  
* `-in`, `--input-dir`: The location of the input CSV files. Subfolders are not processed.  
* `-out`, `--output-dir`: The location of this run's output folder. The script will create a folder with a name containing the date and time in the form `%d-%m-%Y at %H-%M-%S`. The script will then have ALOE output the results of each run to subfolders within the output folder. Each subfolder is named in the form `[affect code]_[pipeline name]`. If the affect code cannot be parsed from the filename, the script will default to the filename itself.  

Options:  
* `-p`, `--pipelines`: Name(s) of the pipelines to be run (default=CSCW2013).  
  e.g. `-p CSCW2013 HeatSegmentationPipeline` will run CSCW2013 on all input files, then HeatSegmentationPipeline on all input files. All results will be output to the single output directory.  
* `-gf`, `--global-flags`: Global pipeline flags to be run with all specified pipelines. Omit the leading '--' 
  e.g. `-gf downsample balance-test-set time-window 30` will pass `--downsample`, `--balance-test-set`, and `--time-window 30` flags to each pipeline specified by the user. 
* `-l`, `--file-limit`: Optionally limit the number of files to be processed. This will process files in whatever order they are supplied by os.listdir(), until the number of files processed is equal to the specified integer.  
* `--debug`: Print extraneous debug information to stdout.  
* `--disable-file-ops`: Disable filesystem operations - *this renders the script inoperable.* Used purely for debugging.  

Known Bugs: Parsing a filenames whose affect codes contain more than one word (e.g. `export_no_affect.csv`) does not find the full affect code name.  

##gen_csv_from_report.py  
*Copied from script docstring*  
This is a script that takes an ALOE report file and generates a CSV row of the statistics values. The user can specify an optional prefix string to be prepended to the CSV row.  

  Usage: `python3 generate_csv_from_report.py report.txt [optional: affect_name] >> out.csv`

This script can be used in conjunction with the redirect utility to compound an archive of results for data analysis in spreadsheet software. It is up to the user to provide the proper column headings.  

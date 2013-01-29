#!/usr/bin/env python3

""" 
This is a batch script that runs the ALOE classifier using a set of 
user-specified pipelines on a set of data files.

It then runs the report files through a separate python script to generate
an output.csv data file, prepended with the affect code and pipeline name.
Note that this script is written in Python 3.3, which now supports
non-destructive file creation.

Incidentally,
--- 
This file is part of ALOE.

ALOE is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

ALOE is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with ALOE.  If not, see <http://www.gnu.org/licenses/>.
"""

__status__ = "Prototype"
__author__ = "Daniel Barella"
__email__ = "dan.barella@gmail.com"
__copyright__ = "Copyright (c) 2013 SCCL, University of Washington (http://depts.washington.edu/sccl)"
__license__ = "GPL"
__version__ = "0.1"

import os
import sys
import argparse
import shlex
import subprocess
from datetime import datetime

#Determines if the program makes any changes to the filesystem.
#Set to True for standard runs, False when debugging
DISABLE_FILE_OPS = False

#Controls extraneous print statements.
#Set to False for standard runs, True when debugging
DEBUG = True

def parse_args():
  """
  Parse the necessary command-line arguments. The ALOE, input, and output directories are required.
  
  It's not great form to require any number of arguments, 
  but it's much safer than assuming directory locations.
  """
  parser = argparse.ArgumentParser(description="Run ALOE pipeline over a set of files and options,"
                                             + " generating a csv output file of tabulated test results.")
  
  #ALOE directory
  parser.add_argument('-aloe', '--aloe-dir', type=str, required=True,
                       help='The top-level ALOE directory')
  
  #Input directory
  parser.add_argument('-in', '--input-dir', type=str, required=True,
                       help='The location of the affect code chatlog dumps')
  
  #Output directory
  parser.add_argument('-out', '--output-dir', type=str, required=True,
                       help='Directory to which ALOE will output')
  
  #Pipeline arguments
  parser.add_argument('-p', '--pipelines', type=str, nargs='+', 
                      default=['CSCW2013'], 
                      help='Name(s) of the pipelines to be run')
  
  #Global pipeline flags
  parser.add_argument('-gf', '--global-flags', type=str, nargs='+', 
                      help='Global pipeline flags to be run with all specified pipelines. '
                         + 'Omit the leading \'--\'. Ex: \"downsample balance-test-set\"')
  
  #File limit
  parser.add_argument('-l', '--file-limit', type=int, 
                      help='Optionally limit the number of files to be processed')
  
  #TODO: Special pipeline flags
  #parser.add_argument('-sf', '--special-flags', type=str, nargs='+', 
  #                    help='Special pipeline flags to be run with only one pipeline')
  
  
  ##--- Internal Script Options ---##
  #Debug statement output
  parser.add_argument('--debug', action='store_true', default=False,
                      help='Print debug information to stdout')
  
  #Filesystem operations
  parser.add_argument('--disable-file-ops', action='store_true', default=False,
                      help='Disable filesystem operations - this renders the script inoperable. '
                         + 'Used for debugging.')
  
  return parser.parse_args()

def make_file(name, directory):
  """
  Create a file with the specified name in the specified directory.
  This method will not overwrite files of the same name.
  
  Returns the path to the file - this is not guaranteed to be an absolute path!
  """
  
  path = os.path.join(directory, name)
  
  try:
    #The 'x' flag was introduced in Python 3.3 - so lower versions are not supported
    file = open(path, 'x')
    file.close()
  except FileExistsError:
    print("The file " + path + " already exists.")
  
  return path

def gen_csv(report_file, csv_prefix=''):
  """
  Takes an ALOE report file and generates a CSV row of the statistics values. 
  The user can specify an optional prefix string to be prepended to the CSV row.
  
  Copy/Pasta from generate_csv_from_report.py
  """
  #For each line, make a list of the data values following each semicolon (whitespace stripped)
  with open(report_file) as file:
    csv = [line.split(':')[1].strip() for line in file if ':' in line]
  
  #Return the list as a comma-separated string, optionally with the prefix from sys.argv[2]
  return (csv_prefix + ",") + (",".join(csv))

def escape_spaces(string):
  """
  Returns a pseudo shell-parseable string where all space characters are escaped by '\'.
  This is purely for debug statements, and should not be used for actual shell calls.
  """
  return r'\ '.join(shlex.split(string))

def main():
  """
  Pseudocode runthrough:
  
  We'll need to have the aloe, input and output directories
  From there, we need a list of the pipelines to run, and potentially the arguments to give as well
  Make sure that there is no outfile already
   Create a blank outfile with timestamp name - throw an error if there is
  
  For each affect dump:
   Get the affect name
   Make a subdir with that affect name
   run the pipe in that subdir (separate method)
   
  Running a pipe:
   make another subdir with the pipe's name, plus any non-global options given to the pipe
    (i.e. running HeatSeg with a different time window)
   
   run the process, output to the subdir
   call gen_csv on the generated report file, prepend the returned string with the affect, 
    pipe and options
  """
  
  #Require Python 3.3 or above
  if sys.version_info[0] < 3 or sys.version_info[1] < 3:
    print('This script requires Python 3.3 or above.')
    sys.exit(1)
  
  args = parse_args()
  
  #Set internal flags
  DEBUG = args.debug
  DISABLE_FILE_OPS = args.disable_file_ops
  
  if DEBUG:
    print("Registered args: " + args.__repr__())
  
  script_origin_dir = os.getcwd()
  
  script_output_folder_name = datetime.now().strftime("%d-%m-%Y at %H-%M-%S")
  script_output_folder = os.path.join(args.output_dir, script_output_folder_name)
  if not DISABLE_FILE_OPS:
    #Create output folder
    print('Creating output folder: ' + script_output_folder) 
    os.makedirs(script_output_folder)
    
    #Create output CSV file
    print('Creating out.csv inside ' + script_output_folder) 
    make_file("out.csv", script_output_folder)
  
  #Changing to the ALOE directory eliminates issues such as sourcing the emoticons file
  os.chdir(args.aloe_dir)
  print("Switched to top-level ALOE directory: " + os.getcwd())
  
  #TODO: Special pipe options
  
  #For each pipe, run on the each affect file in the input directory
  for pipename in args.pipelines:
    #Note the file limit in this loop - this is a user-controlled option
    for filename in os.listdir(args.input_dir)[:args.file_limit]:
      
      #Work only on proper filetypes
      if '.csv' in filename:
        
        try: 
          affect_name = filename.split(('_'))[2].split('.')[0] #!!NOTE!!: This is 100% filename specific!
        except IndexError:
          affect_name = filename
        
        input_file_path = os.path.join(args.input_dir, filename)
        curr_output_subdir = os.path.join(script_output_folder, affect_name) + "_" + pipename
        
        command = [
                   'java', '-jar',
                   os.path.join(args.aloe_dir,"dist/aloe.jar"), pipename, "train", #ALOE call
                   input_file_path, #ALOE input CSV
                   curr_output_subdir #ALOE output directory
                  ] + ['--' + flag if not flag.isnumeric() else flag for flag in args.global_flags] #Global test flags
        if DEBUG:
          print(command)
        
        if not DISABLE_FILE_OPS:
          #Run ALOE
          proc = subprocess.Popen(command)
          proc.wait()
          
          #Do some directory building
          gen_csv_script_path = os.path.join(script_origin_dir, 'generate_csv_from_report.py')
          report_file_path = os.path.join(curr_output_subdir, 'report.txt')
          output_csv_path = os.path.join(script_output_folder, 'out.csv')
          
          command = ['python3', gen_csv_script_path, report_file_path, (affect_name + '_' + pipename)]
          
          if DEBUG:
            print(command)
          
          #Run the CSV-generator, append to output file
          with open(output_csv_path, 'a') as output:
            output.write(gen_csv(report_file_path, affect_name + '_' + pipename) + '\n')
      
      #If the file is not a .csv, we ignore it.
      else:
        print(filename + ' was not processed')

if __name__ == "__main__":
  main()


#!/usr/bin/env python3

""" 
This is a batch script that runs the ALOE classifier with the 
CSCW2013 and HeatSegmentation pipelines on a set of data files.
It then runs the report files through a python script to generate
a csv data file, prepended with the affect code and pipeline.
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
import subprocess
import shlex
from datetime import datetime

#Determines if the program makes any changes to the filesystem.
#Set to True for standard runs, False when debugging
FILE_OPS = False

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
  
  #TODO: Mode - currently only does train
  
  #Global pipeline flags
  parser.add_argument('-gf', '--global-flags', type=str, nargs='?', 
                      help='Global pipeline flags to be run with all specified pipelines. '
                         + 'Omit the leading \'--\'. Ex: \"downsample balance-test-set\"')
  
  #TODO: Special pipeline flags
  #parser.add_argument('-sf', '--special-flags', type=str, nargs='+', 
  #                    help='Special pipeline flags to be run with only one pipeline')
  
  return parser.parse_args()

def make_file(name, directory):
  """
  Create a file with the specified name in the specified directory.
  This method will not overwrite files of the same name.
  """
  
  abs_path = os.path.join(directory, name)
  
  try:
    #The 'x' flag was introduced in Python 3.3 - so lower versions are not supported
    file = open(abs_path, 'x')
    file.close()
  except FileExistsError:
    print("The file " + abs_path + " already exists.")

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
  
  args = parse_args()
  if DEBUG:
    print("Registered args: " + args.__repr__())
  
  output_folder_name = "Output at " + datetime.now().strftime("%H-%M-%S on %d-%m-%Y")
  output_abs_path = os.path.join(args.output_dir, output_folder_name)
  if FILE_OPS:
    #Create output folder
    print('Creating output folder \'' + output_folder_name + '\' at ' + output_abs_path) 
    os.makedirs(output_abs_path)
    
    #Create output CSV file
    print('Creating out.csv inside ' + output_abs_path) 
    make_file("out.csv", output_abs_path)
  
  #ALOE gets grumpy if we're not in its directory
  print("Switching to top-level ALOE directory: " + os.getcwd())
  os.chdir(args.aloe_dir)
  
  #Loop through the files in the input directory
  #TODO: Special pipe options
  for filename in os.listdir(args.input_dir):
    #print(filename)
    for pipename in args.pipelines:
      affect_name = filename.split(('_'))[2].split('.')[0] #This is 100% filename specific
      
      if DEBUG:
        print("java -jar " + escape_spaces(os.path.join(args.aloe_dir,"dist/aloe.jar")) + " " + pipename + " train " #ALOE call
              + escape_spaces(os.path.join(args.input_dir, filename)) + " " #ALOE input directory
              + escape_spaces(os.path.join(output_folder_name, affect_name) + "_" + pipename) #ALOE output directory
             )
      
      if FILE_OPS:
        subprocess.popen()
  

if __name__ == "__main__":
  main()


#!/usr/bin/env python3

""" 
This is a batch script that runs the ALOE classifier with the 
CSCW2013 and HeatSegmentation pipelines on a set of data files.
It then runs the report files through a python script to generate
a csv data file, prepended with the affect code and pipeline.
Note that this script is written in Python 3.

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

import sys
import argparse
import subprocess

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
   call gen_csv on the generated report file, prepend the returned string with the affect, pipe and options
  """
  
  parser = argparse.ArgumentParser(description="Run ALOE pipeline over a set of files and options,"
                                             + " generating a csv output file of tabulated test results.")
  #Pipeline arguments
  parser.add_argument('-p', '--pipelines', type=str, nargs='+', 
                      default=['CSCW2013', 'HeatSegmentationPipeline'], 
                      help='Name(s) of the pipelines to be run')
  
  #ALOE directory
  parser.add_argument('-aloe', '--aloe-dir', type=str, nargs=1,
                       required=True,
                       help='The top-level ALOE directory')
  
  #Input directory
  parser.add_argument('-in', '--input-dir', type=str, nargs=1,
                       required=True,
                       help='The location of the affect code chatlog dumps')
  
  #Output directory
  parser.add_argument('-out', '--output-dir', type=str, nargs=1,
                       required=True,
                       help='Directory to which ALOE will output')
  
  args = parser.parse_args()
  print(args)

if __name__ == "__main__":
  main()


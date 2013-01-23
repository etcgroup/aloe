#!/usr/bin/env python3

"""
This is a script that takes an ALOE report file and generates a CSV row of the statistics values. 
The user can specify an optional prefix string to be prepended to the CSV row.

  Usage: python3 generate_csv_from_report.py report.txt [optional: affect_name] >> out.csv

This script can be used in conjunction with the redirect utility to compound an archive of results
for data analysis in spreadsheet software. It is up to the user to provide the proper column headings.
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

import sys
import fileinput

__status__ = "Alpha"
__author__ = "Daniel Barella"
__email__ = "dan.barella@gmail.com"
__copyright__ = "Copyright (c) 2013 SCCL, University of Washington (http://depts.washington.edu/sccl)"
__license__ = "GPL"
__version__ = "0.2"

def gen_csv():
  #For each line, make a list of the data values following each semicolon (whitespace stripped)
  with open(sys.argv[1]) as file:
    csv = [line.split(':')[1].strip() for line in file if ':' in line]
  
  #Return the list as a comma-separated string, optionally with the prefix from sys.argv[2]
  return sys.argv[2] + "," + (",".join(csv)) if len(sys.argv) == 3 else (",".join(csv))

def main():
  if(len(sys.argv) not in (2, 3)):
    #Printing to stderr prevents concatenating error messages to the output file
    sys.stderr.write("Usage: python3 generate_csv_from_report.py report.txt [optional: affect_name] >> out.csv\n")
    sys.exit(1)
  
  #Print comma-separated string to stdout
  print(gen_csv())

if(__name__ == "__main__"):
  main()


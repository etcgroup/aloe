#!/usr/bin/env python

"""
This is a script that takes an ALOE report file and generates a CSV row of the statistics values. 

This script can be used in conjunction with the cat utility to compound an archive of results
for data analysis in spreadsheet software. It is up to the user to provide the proper headings.
Note that this is written in Python 3.

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

__status__ = "Prototype"
__author__ = "Daniel Barella"
__email__ = "dan.barella@gmail.com"
__copyright__ = "Copyright (c) 2013 SCCL, University of Washington (http://depts.washington.edu/sccl)"
__license__ = "GPL"
__version__ = "0.1"

#Work on exactly one report file
if(len(sys.argv) != 2):
  sys.stderr.write("Usage: python3 generate_csv_from_report.py Report.txt >> out.csv\n")
  sys.exit(1)

#For each line, get the strings following each semicolon (whitespace stripped)
csv = [line.split(':')[1].strip() for line in fileinput.input()]

#Print to stdout with comma separation
print(",".join(csv))

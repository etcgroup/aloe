ALOE 1.0
====

Affect Labeller of Expressions

Given a list of expressions - chat messages with timestamps - ALOE applies one
of about thirty affect labels.

	time, message[, label(s)]
	1:32:40PM, "Hello, how are you", "frustration anger"

Will produce 4 files, two including the labelled messages (one longform and one
shortform), one with a list of the top 10 highly predictive features for
each label, and one with the evaluation relative to the labels optionally
provided (if no labels provided, there will be no evaluation file).

	

The details of this implementation are included in the accompanying
publication, which should also be cited if this tool is used in published
work:

	“Statistical Affect Detection in Collaborative Chat,” Michael Brooks, Katie
	Kuksenok, Megan Torkildson, Daniel Perry, John Robinson, Paul Harris, Ona
	Anicello, Taylor Scott, Ariana Zukowski, Cecilia Aragon. Proceedings of the
	ACM Conference on Computer Supported Cooperative Work, CSCW (2013).


Developing with ALOE

The code is a NetBeans project. When it is opened, it will require adding the
following dependent libraries (which, if you store in a lib/ folder, will be
ignored in any commits and automatically picked up as libraries by netbeans):
* weka 3.6 (http://www.cs.waikato.ac.nz/ml/weka)
* javacsv 2.1 (http://sourceforge.net/projects/javacsv)

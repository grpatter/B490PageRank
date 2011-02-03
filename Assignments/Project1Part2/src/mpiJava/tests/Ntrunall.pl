#!//perl

#
# A quick and dirty perl-script to run all the test codes
#

  @dirs = ('ccl', 'comm', 'dtyp', 'env', 'group', 'pt2pt', 'topo', 'PingPong');

# OK Let loop through all the test cases.

  foreach $filename (@dirs) {

   chdir("$filename"); 		# cd in test directory

   system("NTrunit.pl");	# run the local perl script runit

   chdir("..");			# cd back in tests directory

  }

  print "\n COMPLETED ALL TESTS \n";







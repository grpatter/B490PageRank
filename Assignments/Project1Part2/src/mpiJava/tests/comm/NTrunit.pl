#!/....../perl

#
# A quick and dirty perl-script to compile and run comm test codes
#

  @files = ('attr', 'commdup', 'compare', 'intercomm', 'split');

  $classpath = "-classpath .";		# may be necessary to add ...mpiJava/
					# so that import mpi.*; works

  $WMPIexe 		= "..\\..\\src\\WMPI_JNI\\Release\\WMPI_JNI.exe";
  $localEXE		= "WMPI.exe";
# $java_switches	= "-verbose -depend ";
  $java_switches	= "";
  $class 	 	= ".class";
  $java 	 	= ".java";
  $pg		 	= ".pg";


#
# Let's create a generic config file $$ == process ID
#
  open(F, ">$$.pg") || die "\n Cannot open $$.pg: $! \n ";
  print F "# The Generic WMPI Configuration file \n";
  print F "local 1"; 
  close(F);

#
# copy EXE interface to WMPI to local directory
#
  system("copy  $WMPIexe $localEXE");
#
# OK Let loop through all the tets cases.
# compile then, create a config file and then run them
#
  foreach $filename (@files) {
#
#  compile each .java file
#
   system("javac $java_switches $filename$java");
#
#  Copy the generic $$.pg file to one named after the class we're running
#
   system("copy $$.pg $filename.pg");
#
#  Copy the generic WMPI.exe to one named after the class we're running
#
   system("copy $localEXE $filename.exe"); 
#
#  compose a command line
#
   $command_line = "$filename.exe -p4pg $filename.pg";

# $command_line = "$filename.exe -p4dialog";

#
# Now lets run the code
#

  print "\n Running $filename TEST \n";

  system ($command_line);

  print "Completed $filename  TEST\n";
  }

#
# completed tests, clean up directory
#

  print "\nCompleted tests - cleaning up comm directory \n";

  system("del *.class *.exe *.pg");






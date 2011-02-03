#!/....../perl

#
# A quick and dirty perl-script to compile and run PingPong
#

@files = ('Clock.java', 'PingPong.java', 'Table.java');

#$java_switch = "-verbose";
$java_switch = "";

#
# OK Let compile the source
#

foreach $action (@files) {
  system("javac $java_switch $action");
}

#
# Now lets run the code
#

$command_line = "PingPong.exe -p4gm 4000000 -p4pg PingPong.pg";
#$command_line = "PingPong.exe -p4dialog";

system ($command_line);


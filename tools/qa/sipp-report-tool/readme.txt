How to run:

1 - Compile with 'mvn install'

2 - Copy the file 'target/sipp-report-0.0.1-SNAPSHOT-with-dependencies.jar' to the location of the statistics files produced by SIPP

3 - Run with 'java -jar' and check the available options with '-h' flag

4 - Have fun



How to make SIPP produce statistics files

1 - When starting SIPP use the following flags '-fd 1 -trace_stat'

2 - This will produce a .csv file that can be used by this tool
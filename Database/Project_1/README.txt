Build Instructions
----------------

Needs Java 8 to build/run.  Specifically, it was built with jdk1.8.0_74.
-------------------------------------------------------------------------

1. Unzip the project zip file.

2. Execute the BUILD COMMAND (from command line):
javac *.java -cp vertica_jdbc_7.2.1-0.jar

3. Execute the RUN COMMAND
java CertifyNF <schema filename>

example:
java CertifyNF testschema.txt


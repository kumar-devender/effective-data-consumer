Problem Description
===================

We have supplied you with a small web server. It is written in
Python, you can run it on any system with Python 2.6 or
Python 2.7.  Pretty much any Unix based system will work (e.g.
Linux or a Mac.)  You can probably even use a PC if you want,
but the verification tool may not work.

By default the web server listens on port 7299.

The web server has three endpoints:  
  /source/a  
  /source/b  
  /sink/a  

Source A emits JSON records.  
Source B emits XML records.  
Sink A accepts JSON records.  

Most records from Source A will have a corresponding record from Source B, these are "joined" records.

Some records from one source will not have a match from the other source, these are "orphaned" records.

Some records are malformed, these are "defective" records.

Each source will emit each record ID either 0 or 1 times.

Your program must read all the records from /source/a and
/source/b, categorize them as "joined", "orphaned", or "defective".  
It must report the "joined' and "orphaned" records to /sink/a, It
can ignore defective records, the ordering in which records are
submitted is not important.

By default the test program will emit around 1000 records, once
all the records have been read from an endpoint it responds with
a "done" message.

You must start sending data before the endpoints are done.

Test your program against a much larger data
set, so your program must behave as if it is going to run forever.

Here's the catch: Both sources and the sink endpoints are interlinked.
Sometimes the sources will block until data has been read from or written
to the other endpoints, when this happens the request will return a
406 response.  The program will never deadlock.


Testing
=======
The web server writes your responses and the expected response
into its running directory, There is program to compare the
these two files.

You should test your program with a larger data set.  
To do so run `fixture.py -n NUMBER_OF_RECORDS`, but don't assume that your program knows the size of the data set.


Message Specifications
======================

Endpoint /source/a
------------------
normal record: { "status": "ok", "id": "XXXXX" }
done record: {"status": "done"}

Endpoint /source/b
------------------
normal record:
<?xml version="1.0" encoding="UTF-8"?><msg><id value="$ID"/></msg>

done record:
<?xml version="1.0" encoding="UTF-8"?><msg><done/></msg>

Endpoint /sink/a
----------------
To endpoint in POST body:
{"kind": "$KIND", "id": "$ID"},
where $KIND can be either "joined" or "orphaned", and $ID is the $ID from the originating messages.

Success response:
{"status": "ok"}

Failure response:
{"status": "fail"}


How To Use The Tools
====================

* Execute the web server by running:
    ```
    python fixture.py
    ```

    This is a Python 2 program, so on some systems you may need to call it as:
    ```
    python2 fixture.py
    ```

* By default the fixture emits 1000 records.  You can choose the number of
  records with `-n COUNT` option.  E.g.
    ```
    pyhon fixture.py -n 50000
    ```

* The `fixture.py` program terminates fifteen (15) seconds after both
  sources are done.

* When the fixture terminates it will print a set of counters. These
  values may be useful to you.

* The output will appear in the files `submitted.txt` and `expected.txt`
  in the fixture's execution directory.

* Compare the `submitted.txt` and `expected.txt` files by executing:
    ```
    sh check.sh
    ```

* If all the records match, the comparison program will terminate
  with the message `good` and exit code `0`.

* If there are differences between the expected and submitted records
  then it prints a diff.
    * The expected records appear after the lines starting with `<`.
    * Your records appear after the lines starting with `>`. 
    * After printing the diff, it prints `bad` and terminates with exit code `1`.


Submission
==========

**Test it against a much larger dataset,** so you might want
to try out with `fixture.py` `-n` option.


Prerequisite
==========
* Java 8 or above
* Maven Installed
* Python 2.6 or above

How to run
==========
* Clone the project
* Enter in project root directory
* Run `fixture.py -n NUMBER_OF_RECORDS`
* Run `mvn clean package`
* Run `java -jar target/effective-data-consumer-1.0.0-SNAPSHOT.jar`


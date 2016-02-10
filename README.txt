=================
README: load_Inm		
=================

[Author]: Aron T Fleming
[EMAIL]: arontfleming@gmail.com
[Date]: Nov-15-2013
[Version]: 2.0



CONTENTS
==================================

I.	INSTALL
II.	WHAT DOES THIS PROGRAM DO
III.	HOW TO RUN PROGRAM
IV.	HOW TO CONFIG
V.	OUTPUT AND LOG
VI.	DISPLAY INDICATOR
VII.	TROUBLESHOOT	




I. INSTALL
===========

1. Create a folder and name it whatever you want.

2. Copy files load_Inm_V2.jar, config.ini, and RunWin.bet(for windows) or RunOSX.command(for OSX) in to the folder that was created earlier.

3. Set up config.ini by follow the steps from IV. HOW TO CONFIG in this README.

** For mac user, if the user would like to run program by double click RunOSX.command, please follow addition step below.
	* Open Terminal shell in the Applications folder.
	* Navigate to a folder that contain the program.
	* Type in the command line: chmod +x RunOSX.command
	* Press return.




II.  WHAT DOES THIS PROGRAM DO
===============================

	This is a program that was created for download all inmate information from Illinois Department of Corrections website (i.e. websucker). Also, the program is able to generate spreadsheet from the data that was collected. All the output file will organize in to a folder that have a date as a folder name. 	
	The program processes could divided into three phases. The first phase is "get inmate list". It will connect to the website, then use the first letter of the inmate last name that user provide in config.ini as input for the website to obtain the list of inmate. Second phase is “get inmate detail”. it uses the innate id that obtained earlier from first phase as an input to extract the inmate information from website.  Last phase is “create spreadsheet”. The program will combine all inmate information in to one spreadsheet.




III.  HOW TO RUN PROGRAM
=========================

.I Double click to run
	For Windows: Double click at file "RunWin.bat". The command prompt window will automatically pop up to show the process status.

	For Mac: Double click at file "RunOSX.bat". The terminal shell will automatically pop up to show the process status.

.II Run from command line
	- Open command line program.
	- Navigate to a folder that contain the program.
	- Type in command: java -jar load_Inm_V2.jar
	



IV.  HOW TO CONFIG
===================

In order to run the program, user must set up the program first by provide the request parameters in file "config.ini".
To config the program, open file config.ini with any text editor tool.

	GET_LIST: It takes the first character after "=" as an input [Y or N only] (upper case sensitive).
		Y => Run the first phase "get inmate list".
		N => Skip "get inmate list" and go the the next phase.  

	GET_DETAIL: It takes the first character after "=" as an input [Y or N only] (upper case sensitive).
		Y => Run the second phase "get inmate detail.
		N => Skip "get inmate detail and go the the next phase.  

	GET_CSV: It take the first character after "=" as an input [Y or N only] (upper case sensitive).
		Y => Run the first phase "create spreadsheet".
		N => Skip "create spreadsheet" and go the the next step in the program.
  
	LASTNAME: it takes a list of alphabet letter (upper case sensitive) after "=" as an input.
		example => LASTNAME=A, B, C, D 
		example => LASTNAME=A,B,C,D,E,F
		example => LASTNAME=ABCDE 

	LIST_FOLDER: it takes a path after "=" as path file for save the inmate list data. 
		The program will create new folder according to the path if the folder in the path does not exit.  
		Default path => LIST_FOLDER=data/1_list
		
	DETAIL_FOLDER: it takes a path after "=" as path file for save the inmate information data. 
		The program will create new folder according to the path if the folder in the path does not exit.  
		Default path => DETAIL_FOLDER=data/2_detail

	CSV_FOLDER: it takes a path after "=" as path file for save the spreadsheet. 
		The program will create new folder according to the path if the folder in the path does not exit.  
		Default path => CSV_FOLDER=csv
				



V.  OUTPUT AND LOG
===================

OUTPUT: 
- The first phase generate two file output .html and .txt file that contain list of inmate in a folder according to GET_LIST. 
 .html file is a file that contain the raw HTML file that program obtain from website, and it’s file name will has the create date as prefix follow by one alphabet letter and “_raw.html” as suffix.  
 .txt file is a list of inmate. The file name will has the create date as prefix follow by one alphabet letter.

- Second phase also generate two file output .html and .txt file that contain the information for each inmate in a folder according to GET_DETAIL.
 .html file is a file that contain the raw HTML file that program obtain from website, and it’s file name will has the create date as prefix follow by inmate id.  
 .txt file is a file that contain inmate information. The file name will has the create date as prefix follow by inmate id and “_parsed.txt” as suffix.  

- Last phase generate .csv file as a spreadsheet, and it will has create date as a file name.


LOG:
- Log is a log file that contain all activity and error of the program. It will has a time stamp for each process, and it has a date as a file name. 




VI.  DISPLAY INDICATER
=======================
	
*************************************************************************
*			Command prompt windows				*
*************************************************************************
*									*
*Start									*   <- 1.
*									*
*process -> [B] [Q] |Complete|						*   <- 2.
*									*
*process -> [131113_B.txt]: 10 20 30 40 50 60 70 80 90 100 |Complete|	*   <- 3.
*process -> [131113_Q.txt]: 10 20 30 40 50 60 70 80 90 100 |Complete|	*   <- 4.
*									*
*process -> [csv]: 10 20 30 40 50 60 70 80 90 100 |Complete|		*   <- 5.
*									*
*Finished								*   <- 6.
*Process time: 2066.962000 sec						*   <- 7.
*									*
*									*
*************************************************************************

1. Program start
2. Processing first phase, get inmate list.
3. Processing second phase at file 131113_B.txt, number, get inmate information. 
4. Processing second phase at file 131113_Q.txt, number, get inmate information. 
5. Processing third phase, generate .csv file.
6. Program finish
7. To indicate how long it take from start to finish the program.
+ Number 10 - 100 is indicate the percentage of processing.




VII.  TROUBLESHOOT
===================

	- If file RunWin.bat or RunOSX.command could not find the classpath, user can add classpath by follow the steps below.
		* Open file RunWin.bat or RunOSX.command with any text editor.
		* Add: -cp [classpath] between java and -jar -> Example: java -cp folder/load_Inm_V2.jar -jar load_Inm_V2.jar
		* Save and close text editor.


	- For mac user, if file RunOSX.command could not double click to run because it does not have permission or could not access, please try step below.
		* Open Terminal shell in the Applications folder.
		* Navigate to a folder that contain file RunOSX.command.
		* Type in the command line: chmod +x RunOSX.command
		* Press return.



===================================================

# Overview
This application updates the product fields inside an Excel document based on a MYSQL table.
 
## Build and run
* Open and build project using IntelliJ.
* Run application.
    * The first run asks for database credentials. You can find them using the instructions in the section bellow.  
    * Database name
        * Enotites -> Organwsi -> Etairies -> Metaboli etairias -> Epilogi etairias -> see Prefix
    * Username and hostname:
         * Enotites -> Organwsi -> Systima -> Sql server setup

## External libraries
* JDBC
* Apache Commons Codec

## Useful Links
* [Fix for "invalid signature file digest.."](https://stackoverflow.com/questions/34855649/invalid-signature-file-digest-for-manifest-main-attributes-exception-while-tryin/34856095#34856095)

## Excel structure
* These are the necessary changes that need to be done:
    * One more column added to the original excel (as first) that indicates if the row has been updated.
    * Delete extra rows with empty content

## TODO
	- unit tests
	- check format of excel
	- create a json that defines columns
	- Issues after update 
	    - problem dutring update since there are two columns for the quantity.
    	- investigate what happened with in the case of 2 barcodes   
	    - dates were not updated
	 - Requested
        - log quantity changes to a file
        - fix font size when adding new lines
	    


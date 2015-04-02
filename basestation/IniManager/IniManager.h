
#ifndef INIMANAGER_H
#define INIMANAGER_H

#define INTERNAL_BUF_SIZE 250

/** A simple ini file manager.
*
* This is a simple ini file manager intended for low duty cycle usage. 
*
* It follows an old "Windows" style of ini file format with section, key, and value.
* This version only operates on strings at this time.
*
* As a "simple" ini file manager, this version does not cache anything internally.
* This comes at the "cost" that each write transaction will read and replace the
* ini file. Read transactions will open and scan the file.
*
* Also, an internal stack-frame buffer is used to manage the read operations. As
* such, no single record in the file can exceed this buffer size (compile time set
* with a default of 250 bytes). A single record for a section is surrounded with
* '[' and ']' and a new line appended. A single record for an entry within a
* section for a key, value pair is separated with an '=' and a new line appended.
* @code
* [section name]
* Key name=value for Key name
* Another key=another value
* @endcode
*/

class INI
{
public:
    /** Constructor for an INI file interface.
    *
    * Constructor for an INI file interface.
    *
    * @param[in] file is the filename to manage. Memory is allocated to hold
    *       a private copy of the filename. Be sure that this parameter
    *       has the right path prefix based on what file system you have.
    */
    INI(const char * file = NULL);

    /** destructor for the ini manager.
    *
    * releases the memory allocation.
    */
    ~INI(void);

    /** Determine if a file exists
    *
    * This API can be used to determine if a file exists. The file may
    * be specified as a parameter, but if no parameter is supplied it will
    * then check to see if the INI file exists. This is either the file
    * passed to the constructor, or the file passed to the SetFile API.
    *
    * @param[in] file is the optional filename to check for existance.
    * @returns true if the file exists.
    */
    bool Exists(const char * file = NULL);

    /** set the file to use
    *
    * If not set at the time of construction, or if a change is needed, this
    * API can be used.
    *
    * @param[in] file is the filename to manage.
    * @returns true if success, false if memory could not be allocated.
    */
    bool SetFile(const char * file);

    /** Read a string from the ini file - if it exists.
    *
    * This searches the ini file for the named section and key and if found it will
    * return the string associated with that entry into a user supplied buffer.
    * 
    * @param[in] section is the name of the section to search.
    * @param[in] key is the name of the key to search.
    * @param[out] buffer is the caller provided buffer for this method to put the string into.
    * @param[in] bufferSize is the caller provided declaration of the available space.
    * @param[in] defaultString is an optional parameter that sets the buffer if the section/key is not found.
    * 
    * @return true if the section, key, and value are found AND the value will fit in the buffer
    *       in which case it is written into the buffer; false otherwise.
    */
    bool ReadString(const char * section, const char * key, char * buffer, size_t bufferSize, const char * defaultString = NULL);

    /** Writes a string into the ini file
    *
    * This writes a given string into an ini file in the named section and key.
    * 
    * @param[in] section is the name of the section to search.
    * @param[in] key is the name of the key to search.
    * @param[in] buffer is the caller provided buffer containing the string to write. If
    *       buffer is NULL, then any existing entry is removed.
    *
    * @return true if the write was successful; false otherwise.
    */
    bool WriteString(const char * section, const char * key, char * buffer);

private:
    char * iniFile;

    /** Cleanup temporary files.
    *
    * This will attempt to clean up any temporary files. This can happen
    * while writing a new file, if something went wrong and the program 
    * crashed or otherwise could not complete the process.
    * This will look for the temp files, try to finish processing them
    * and remove the extraneous.
    *
    * @return true, always until I find a reason not to.
    */
    bool CleanUp();
    
    /** Rename a file
    *
    * This version also works on the local file system.
    *
    * @param[in] oldfname is the old file name
    * @param[in] newfname is the new file name
    * @returns 0 on success, -1 on error
    */
    int Rename(const char *oldfname, const char *newfname);
    
    /** Copy a file
    *
    * This version also works on the local file system.
    *
    * @param[in] src is the source file
    * @param[in] dst is the destination file
    * @returns 0 on success, -1 on error
    */
    int Copy(const char *src, const char *dst);
};

#endif // INIMANAGER_H

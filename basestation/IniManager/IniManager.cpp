// Simple INI file manager.
//
#ifdef WIN32
#include "string.h"
#include "stdlib.h"
#include "stdio.h"
#else
#include "mbed.h"
#endif

#include "IniManager.h"

//#define DEBUG "INI "      //Debug is disabled by default

#include <cstdio>
#if (defined(DEBUG) && !defined(TARGET_LPC11U24))
#define DBG(x, ...)  std::printf("[DBG %s %3d] "x"\r\n", DEBUG, __LINE__, ##__VA_ARGS__);
#define WARN(x, ...) std::printf("[WRN %s %3d] "x"\r\n", DEBUG, __LINE__, ##__VA_ARGS__);
#define ERR(x, ...)  std::printf("[ERR %s %3d] "x"\r\n", DEBUG, __LINE__, ##__VA_ARGS__);
#define INFO(x, ...) std::printf("[INF %s %3d] "x"\r\n", DEBUG, __LINE__, ##__VA_ARGS__);
#else
#define DBG(x, ...)
#define WARN(x, ...)
#define ERR(x, ...)
#define INFO(x, ...)
#endif

INI::INI(const char * file)
    : iniFile(0)
{
    SetFile(file);
}


INI::~INI(void)
{
    if (iniFile)
        free(iniFile);
}


bool INI::Exists(const char * file)
{
    if (file == NULL)
        file = iniFile;
    INFO("Exists(%s)", file);
    FILE * fp = fopen(file, "r");
    if (fp) {
        fclose(fp);
        INFO("  [%s] exists", file);
        return true;
    } else {
        INFO("  [%s] does not exist", file);
        return false;
    }
}


bool INI::SetFile(const char * file)
{
    INFO("SetFile(%s)", file);
    if (file) {
        if (iniFile)
            free(iniFile);
        iniFile = (char *)malloc(strlen(file)+1);
        if (iniFile) {
            strcpy(iniFile, file);
            INFO("  SetFile(%s) success", iniFile);
            return true;
        }
        else {
            iniFile = NULL;
            ERR("  SetFile(%s) failed to allocate memory", file);
        }
    }
    return false;
}

bool INI::ReadString(const char * section, const char * key, char * buffer, size_t bufferSize, const char * defaultString)
{
    bool found = false;
    if (!iniFile)
        return found;
    CleanUp();
    INFO("ReadString from %s", iniFile);
    FILE * fp = fopen(iniFile,"rt");
    if (fp) {
        char buf[INTERNAL_BUF_SIZE];
        bool inSection = (section == NULL) ? true : false;

        while(fgets(buf, sizeof(buf), fp)) {
            int x = strlen(buf) - 1;        // remove trailing \r\n combinations
            while (x >= 0 && buf[x] < ' ')
                buf[x--] = '\0';
            INFO("  reading \"%s\"", buf);
            if (inSection && buf[0] != '[') {
                char * eq = strchr(buf, '=');
                if (eq) {
                    *eq++ = '\0';
                    if ( (strcmp(buf,key) == 0) && (strlen(eq) <= bufferSize) ) {
                        strcpy(buffer, eq);
                        memset(buf, 0, INTERNAL_BUF_SIZE);  // secure the memory space
                        found = true;
                        break;
                    }
                }
            } else {
                if (buf[0] == '[') {
                    char * br = strchr(buf, ']');
                    inSection = false;
                    if (br) {
                        *br = '\0';
                        if (strcmp(buf+1, section) == 0)
                            inSection = true;
                    }
                }
            }
        }
        fclose(fp);
    } else { INFO("NO FOPEN"); }
    if (!found && defaultString != NULL && *defaultString) {
        strncpy(buffer, defaultString, bufferSize);
        buffer[bufferSize-1] = '\0';
        INFO("  sub %s.", buffer);
        found = true;
    }
    return found;
}

bool INI::CleanUp()
{
    char * newFile = (char *)malloc(strlen(iniFile)+1);
    char * bakFile = (char *)malloc(strlen(iniFile)+1);

    if (newFile && bakFile) {
        INFO("CleanUp");
        strcpy(bakFile, iniFile);
        strcpy(newFile, iniFile);
        strcpy(bakFile + strlen(bakFile) - 4, ".bak");
        strcpy(newFile + strlen(newFile) - 4, ".new");

        if (Exists(newFile)) {
            int i;
            i = i;    // suppress warning about i not used when !DEBUG
            // helps recover if the system crashed before it could swap in the new file
            INFO("  *** found %s, repairing ...", newFile);
            i = remove(bakFile);            // remove an old .bak
            INFO("  remove(%s) returned %d", bakFile, i);
            i = Rename(iniFile, bakFile);   // move the existing .ini to .bak
            INFO("  rename(%s,%s) returned %d", iniFile, bakFile, i);
            i = Rename(newFile, iniFile);   // move the new .new to .ini
            INFO("  rename(%s,%s) returned %d", newFile, iniFile, i);
        } else {
            // nothing to do, move on silently.
        }
    }
    free(newFile);
    free(bakFile);
    return true;
}

// Create the new version as .new
// once complete, if something actually changed, then rename the .ini to .bak and rename the .new to .ini
// once complete, if nothing actually changed, then delete the .new
//
bool INI::WriteString(const char * section, const char * key, char * value)
{
    bool found = false;
    bool fileChanged = false;

    INFO("WriteString(%s,%s,%s)", section, key, value);
    if (!iniFile || (value != NULL && strlen(value) > INTERNAL_BUF_SIZE))
        return found;

    char * newFile = (char *)malloc(strlen(iniFile)+1);
    char * bakFile = (char *)malloc(strlen(iniFile)+1);
    if (!newFile)
        return found;       // no memory
    if (!bakFile) {
        free(newFile);
        return found;
    }
    strcpy(bakFile, iniFile);
    strcpy(newFile, iniFile);
    strcpy(bakFile + strlen(bakFile) - 4, ".bak");
    strcpy(newFile + strlen(newFile) - 4, ".new");

    CleanUp();

    INFO("  Opening [%s] and [%s]", iniFile, newFile);
    FILE * fi = fopen(iniFile, "rt");
    FILE * fo = fopen(newFile, "wt");
    if (fo) {
        char buf[INTERNAL_BUF_SIZE];
        bool inSection = (section == NULL) ? true : false;

        if (fi) {
            INFO("  %s opened for reading", iniFile);
            while(fgets(buf, sizeof(buf), fi)) {
                // if not inSection, copy across
                // if inSection and not key, copy across
                // if InSection and key, write new value (or skip if value is null)
                int x = strlen(buf) - 1;        // remove trailing \r\n combinations
                while (x >= 0 && buf[x] < ' ')
                    buf[x--] = '\0';
                
                if (inSection && buf[0] != '[') {
                    char * eq = strchr(buf, '=');
                    if (eq) {
                        *eq++ = '\0';
                        if (strcmp(buf,key) == 0) {
                            if (value != NULL && strcmp(eq, value) != 0) {
                                // replace the old record
                                if (value != NULL) {
                                    fprintf(fo, "%s=%s\n", key, value);
                                    printf("write: %s=%s\r\n", key, value);
                                    INFO("  write: %s=%s", key, value);
                                }
                            }
                            fileChanged = true;
                            inSection = false;
                            found = true;
                        } else {
                            // write old record
                            fprintf(fo, "%s=%s\n", buf, eq);
                            INFO("  write: %s=%s", buf, eq);
                        }
                    } else {
                        // what to do with unknown record(s)?
                        // fprintf(fo, "%s\n", buf);    // eliminate them
                    }
                } else {
                    if (buf[0] == '[') {
                        char * br = strchr(buf, ']');
                        if (inSection) { // found next section while in good section
                            // Append new record to desired section
                            if (value != NULL) {
                                fprintf(fo, "%s=%s\r\n", key, value);
                                INFO("  write: %s=%s", key, value);
                                fileChanged = true;
                            }
                            found = true;
                        }
                        inSection = false;
                        // write old record
                        fprintf(fo, "%s\r\n", buf);
                        INFO("  write: %s", buf);
                        if (br) {
                            *br = '\0';
                            if (strcmp(buf+1, section) == 0)
                                inSection = true;
                        }
                    } else {
                        // copy unaltered records across
                        if (buf[0]) {
                            fprintf(fo, "%s\r\n", buf);
                            INFO("  write: %s", buf);
                        }
                    }
                }
            }
            INFO("close %s", iniFile);
            fclose(fi);
        } else {
            INFO("  %s did not previously exist.", iniFile);
        }
        if (!found) {
            // No old file, just create it now
            if (value != NULL) {
                if (!inSection) {
                    fprintf(fo, "[%s]\r\n", section);
                    INFO("  write: [%s]", section);
                }
                fprintf(fo, "%s=%s\r\n", key, value);
                INFO("  write: %s=%s", key, value);
                fileChanged = true;
            }
            found = true;
        }
        INFO("  close %s", newFile);
        fclose(fo);
    } else {
        ERR("*** Failed to open %s", newFile);
    }
    if (fileChanged) {
        INFO("  File changed: remove bak, rename ini to bak, rename new to ini");
        remove(bakFile);            // remove an old .bak
        INFO("  a");
        Rename(iniFile, bakFile);   // move the existing .ini to .bak
        INFO("  b");
        Rename(newFile, iniFile);   // move the new .new to .ini
        INFO("  c");
        #ifdef RTOS_H
        Thread::wait(1000);
        #else
        wait(1);
        #endif
        INFO("  d");
    }
    free(newFile);
    free(bakFile);
    return found;
}


//***********************************************************
// Private version that also works with local file system
// by copying one file to the other.
//    Returns -1 = error; 0 = success
//***********************************************************
int INI::Rename(const char *oldfname, const char *newfname)
{
    int retval = 0;

    INFO("Rename(%s,%s)", oldfname, newfname);
    if (Copy(oldfname, newfname) == 0) {
        remove(oldfname);
        retval = 0;
    } else {
        retval = -1;
    }
    return (retval);
}

//***********************************************************
// Private version that also works with local file system
//            Returns -1 = error; 0 = success
//***********************************************************
int INI::Copy(const char *src, const char *dst)
{
    int retval = 0;
    int ch;

    INFO("Copy(%s,%s)", src, dst);
    FILE *fpsrc = fopen(src, "r");   // src file
    FILE *fpdst = fopen(dst, "w");   // dest file

    if (fpsrc) {
        INFO("  c1a");
        if (fpdst) {
            INFO("  c1b");
            while (1) {                  // Copy src to dest
                ch = fgetc(fpsrc);       // until src EOF read.
                if (ch == EOF) break;
                fputc(ch, fpdst);
            }
            INFO("  c2");
        fclose(fpsrc);
        fclose(fpdst);
        }
    }
    INFO("  c3");

    if (Exists(dst)) {
        retval = 0;
    } else {
        retval = -1;
    }
    INFO("  c4");
    return (retval);
}


#if 0
// Test code for basic regression testing
//
#include <stdio.h>
#include <assert.h>
#include <string.h>

#include "INI.h"

#define TESTFILE "test.ini"

int main(int argc, char * argv[])
{
    FILE * fp;
    char buffer[100];
    INI ini(TESTFILE);

    // Start testing
    _unlink(TESTFILE);
    assert(ini.ReadString("Section 1", "Name 1", buffer, sizeof(buffer)) == false);

    fp = fopen(TESTFILE, "wt");
    assert(fp);
    fprintf(fp, "[Section 1]\n");
    fprintf(fp, "Name 1=Value 1\n");
    fprintf(fp, "Name 2=Value 2\n");
    fprintf(fp, "\n");
    fprintf(fp, "[Section 2]\n");
    fprintf(fp, "Name 1=Value 2\n");
    fprintf(fp, "Name 2=Value 2\n");
    fprintf(fp, "Name 3=Value 3\n");
    fprintf(fp, "\n");
    fclose(fp);

    assert(ini.ReadString("Section 2", "Name 2", buffer, sizeof(buffer)) == true);
    assert(strcmp("Value 2", buffer) == 0);

    assert(ini.ReadString("Section 3", "Name", buffer, sizeof(buffer)) == false);
    assert(ini.ReadString("Section 1", "Name 3", buffer, sizeof(buffer)) == false);

    assert(ini.WriteString("Section 1", "Name 4", "Value 4") == true);
    assert(ini.ReadString("Section 1", "Name 2", buffer, sizeof(buffer)) == true);
    assert(ini.ReadString("Section 1", "Name 3", buffer, sizeof(buffer)) == false);
    assert(ini.ReadString("Section 1", "Name 4", buffer, sizeof(buffer)) == true);
    assert(strcmp("Value 4", buffer) == 0);

    assert(ini.WriteString("Section 1", "Name 4", NULL) == true);
    assert(ini.ReadString("Section 1", "Name 4", buffer, sizeof(buffer)) == false);

    return 0;
}
#endif

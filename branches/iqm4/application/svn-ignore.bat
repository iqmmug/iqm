:: Run this command for ignoring the file patterns declared in .svnignore.
:: This command has to be run each time a folder is added to the repo!
:: Run this command e.g. after creating an eclipse project (target folder and .setting ,.. .is created)
svn propset -R svn:ignore -F .svnignore .

:: list the properties using 
:: svn propget svn:ignore .

:: Run this command in order to set the subversion keywords to ALL files!
:: Use with caution, since this sets the following svn keywords: Date, Author, Id, HeadURL, Revision
:: svn propset -R svn:keywords -F .svnkeywords iqm-* *

pause
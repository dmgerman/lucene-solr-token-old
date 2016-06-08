Lucene-Sandbox README file



INTRODUCTION

Lucene is a Java full-text search engine.  Lucene is not a complete
application, but rather a code library and API that can easily be used
to add search capabilities to applications.

The Lucene Sanbox is a place for contributions that are not part of
the core Lucene distribution. These contributions will not be part of
distribution.
The Sandbox is also for creating new subprojects in Lucene that may
require restructuring of Lucene code.

NOTE: All contributions must be under the APL license to be part of this
repository.

The basic structure for the respository is

jakarta-lucene-sandbox/README.txt
jakarta-lucene-sandbox/LICENSE.txt
jakarta-lucene-sandbox/index.html
jakarta-lucene-sandbox/CHANGES.txt

jakarta-lucene-sandbox/contributions/CHANGES.txt
jakarta-lucene-sandbox/contributions/build/build.xml
jakarta-lucene-sandbox/contributions/docs/...
jakarta-lucene-sandbox/contributions/[contribution]/src/...
jakarta-lucene-sandbox/contributions/[contribution]/xdocs/about[contribution].xml
jakarta-lucene-sandbox/contributions/[contribution]/build.xml
jakarta-lucene-sandbox/contributions/[contribution]/README.txt

jakarta-lucene-sandbox/projects/[project]/src/...
jakarta-lucene-sandbox/projects/[project]/docs/...
jakarta-lucene-sandbox/projects/[project]/xdocs/...
jakarta-lucene-sandbox/projects/[project]/build.xml
jakarta-lucene-sandbox/projects/[project]/README.txt
jakarta-lucene-sandbox/projects/[project]/CHANGES.txt
jakarta-lucene-sandbox/projects/[project]/LICENSE.txt

Where [contribution] is the name of the contribution
[project] is the name of the subproject in the sandbox area.


The Lucene web site is at:
  http://jakarta.apache.org/lucene

Please join the Lucene-User mailing list by sending a message to:
  lucene-user-subscribe@jakarta.apache.org

FILES

lucene-XX.jar
  The compiled lucene library.

docs/index.html
  The contents of the Lucene website.

docs/api/index.html
  The Javadoc Lucene API documentation.

src/java
  The Lucene source code.

src/demo
  Some example code.

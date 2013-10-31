begin_unit
begin_package
DECL|package|org.apache.lucene.validation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|validation
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|BuildException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Task
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|types
operator|.
name|Resource
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|types
operator|.
name|ResourceCollection
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|types
operator|.
name|resources
operator|.
name|FileResource
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|types
operator|.
name|resources
operator|.
name|Resources
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|util
operator|.
name|FileNameMapper
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|XMLReaderFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CodingErrorAction
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_comment
comment|/**  * An Ant task to verify that the '/org/name' keys in ivy-versions.properties  * are sorted lexically and are neither duplicates nor orphans, and that all  * dependencies in all ivy.xml files use rev="${/org/name}" format.  */
end_comment
begin_class
DECL|class|LibVersionsCheckTask
specifier|public
class|class
name|LibVersionsCheckTask
extends|extends
name|Task
block|{
DECL|field|IVY_XML_FILENAME
specifier|private
specifier|static
specifier|final
name|String
name|IVY_XML_FILENAME
init|=
literal|"ivy.xml"
decl_stmt|;
DECL|field|COORDINATE_KEY_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|COORDINATE_KEY_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(/[^/ \t\f]+/[^=:/ \t\f]+).*"
argument_list|)
decl_stmt|;
DECL|field|BLANK_OR_COMMENT_LINE_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|BLANK_OR_COMMENT_LINE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[ \t\f]*(?:[#!].*)?"
argument_list|)
decl_stmt|;
DECL|field|TRAILING_BACKSLASH_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|TRAILING_BACKSLASH_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[^\\\\]*(\\\\+)$"
argument_list|)
decl_stmt|;
DECL|field|LEADING_WHITESPACE_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|LEADING_WHITESPACE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[ \t\f]+(.*)"
argument_list|)
decl_stmt|;
DECL|field|WHITESPACE_GOODSTUFF_WHITESPACE_BACKSLASH_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|WHITESPACE_GOODSTUFF_WHITESPACE_BACKSLASH_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[ \t\f]*(.*?)(?:(?<!\\\\)[ \t\f]*)?\\\\"
argument_list|)
decl_stmt|;
DECL|field|TRAILING_WHITESPACE_BACKSLASH_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|TRAILING_WHITESPACE_BACKSLASH_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(.*?)(?:(?<!\\\\)[ \t\f]*)?\\\\"
argument_list|)
decl_stmt|;
comment|/**    * All ivy.xml files to check.    */
DECL|field|ivyXmlResources
specifier|private
name|Resources
name|ivyXmlResources
init|=
operator|new
name|Resources
argument_list|()
decl_stmt|;
comment|/**    * Centralized Ivy versions properties file    */
DECL|field|centralizedVersionsFile
specifier|private
name|File
name|centralizedVersionsFile
decl_stmt|;
comment|/**    * License file mapper.    */
DECL|field|licenseMapper
specifier|private
name|FileNameMapper
name|licenseMapper
decl_stmt|;
comment|/**    * A logging level associated with verbose logging.    */
DECL|field|verboseLevel
specifier|private
name|int
name|verboseLevel
init|=
name|Project
operator|.
name|MSG_VERBOSE
decl_stmt|;
comment|/**    * Failure flag.    */
DECL|field|failures
specifier|private
name|boolean
name|failures
decl_stmt|;
comment|/**    * All /org/name version keys found in ivy-versions.properties, and whether they    * are referenced in any ivy.xml file.    */
DECL|field|referencedCoordinateKeys
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|referencedCoordinateKeys
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Adds a set of ivy.xml resources to check.    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|ResourceCollection
name|rc
parameter_list|)
block|{
name|ivyXmlResources
operator|.
name|add
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
DECL|method|setVerbose
specifier|public
name|void
name|setVerbose
parameter_list|(
name|boolean
name|verbose
parameter_list|)
block|{
name|verboseLevel
operator|=
operator|(
name|verbose
condition|?
name|Project
operator|.
name|MSG_INFO
else|:
name|Project
operator|.
name|MSG_VERBOSE
operator|)
expr_stmt|;
block|}
DECL|method|setCentralizedVersionsFile
specifier|public
name|void
name|setCentralizedVersionsFile
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|centralizedVersionsFile
operator|=
name|file
expr_stmt|;
block|}
comment|/**    * Execute the task.    */
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|BuildException
block|{
name|log
argument_list|(
literal|"Starting scan."
argument_list|,
name|verboseLevel
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|int
name|errors
init|=
name|verifySortedCentralizedVersionsFile
argument_list|()
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|int
name|checked
init|=
literal|0
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Iterator
argument_list|<
name|Resource
argument_list|>
name|iter
init|=
operator|(
name|Iterator
argument_list|<
name|Resource
argument_list|>
operator|)
name|ivyXmlResources
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Resource
name|resource
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|resource
operator|.
name|isExists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Resource does not exist: "
operator|+
name|resource
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|resource
operator|instanceof
name|FileResource
operator|)
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Only filesystem resources are supported: "
operator|+
name|resource
operator|.
name|getName
argument_list|()
operator|+
literal|", was: "
operator|+
name|resource
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|File
name|ivyXmlFile
init|=
operator|(
operator|(
name|FileResource
operator|)
name|resource
operator|)
operator|.
name|getFile
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|checkIvyXmlFile
argument_list|(
name|ivyXmlFile
argument_list|)
condition|)
block|{
name|failures
operator|=
literal|true
expr_stmt|;
name|errors
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Exception reading file "
operator|+
name|ivyXmlFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|checked
operator|++
expr_stmt|;
block|}
name|log
argument_list|(
literal|"Checking for orphans in "
operator|+
name|centralizedVersionsFile
operator|.
name|getName
argument_list|()
argument_list|,
name|verboseLevel
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|entry
range|:
name|referencedCoordinateKeys
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|coordinateKey
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|boolean
name|isReferenced
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isReferenced
condition|)
block|{
name|log
argument_list|(
literal|"ORPHAN coordinate key '"
operator|+
name|coordinateKey
operator|+
literal|"' in "
operator|+
name|centralizedVersionsFile
operator|.
name|getName
argument_list|()
operator|+
literal|" is not found in any "
operator|+
name|IVY_XML_FILENAME
operator|+
literal|" file."
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
name|failures
operator|=
literal|true
expr_stmt|;
name|errors
operator|++
expr_stmt|;
block|}
block|}
name|log
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Checked that %s has lexically sorted "
operator|+
literal|"'/org/name' keys and no duplicates or orphans, and scanned %d %s "
operator|+
literal|"file(s) for rev=\"${/org/name}\" format (in %.2fs.), %d error(s)."
argument_list|,
name|centralizedVersionsFile
operator|.
name|getName
argument_list|()
argument_list|,
name|checked
argument_list|,
name|IVY_XML_FILENAME
argument_list|,
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|/
literal|1000.0
argument_list|,
name|errors
argument_list|)
argument_list|,
name|errors
operator|>
literal|0
condition|?
name|Project
operator|.
name|MSG_ERR
else|:
name|Project
operator|.
name|MSG_INFO
argument_list|)
expr_stmt|;
if|if
condition|(
name|failures
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Lib versions check failed. Check the logs."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns true if the "/org/name" coordinate keys in ivy-versions.properties    * are lexically sorted and are not duplicates.    */
DECL|method|verifySortedCentralizedVersionsFile
specifier|private
name|boolean
name|verifySortedCentralizedVersionsFile
parameter_list|()
block|{
name|log
argument_list|(
literal|"Checking for lexically sorted non-duplicated '/org/name' keys in: "
operator|+
name|centralizedVersionsFile
argument_list|,
name|verboseLevel
argument_list|)
expr_stmt|;
specifier|final
name|InputStream
name|stream
decl_stmt|;
try|try
block|{
name|stream
operator|=
operator|new
name|FileInputStream
argument_list|(
name|centralizedVersionsFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Centralized versions file does not exist: "
operator|+
name|centralizedVersionsFile
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
comment|// Properties files are encoded as Latin-1
specifier|final
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"ISO-8859-1"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|BufferedReader
name|bufferedReader
init|=
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
name|String
name|currentKey
init|=
literal|null
decl_stmt|;
name|String
name|previousKey
init|=
literal|null
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|null
operator|!=
operator|(
name|line
operator|=
name|readLogicalPropertiesLine
argument_list|(
name|bufferedReader
argument_list|)
operator|)
condition|)
block|{
specifier|final
name|Matcher
name|keyMatcher
init|=
name|COORDINATE_KEY_PATTERN
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|keyMatcher
operator|.
name|matches
argument_list|()
condition|)
block|{
continue|continue;
comment|// Ignore keys that don't look like "/org/name"
block|}
name|currentKey
operator|=
name|keyMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|previousKey
condition|)
block|{
name|int
name|comparison
init|=
name|currentKey
operator|.
name|compareTo
argument_list|(
name|previousKey
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|comparison
condition|)
block|{
name|log
argument_list|(
literal|"DUPLICATE coordinate key '"
operator|+
name|currentKey
operator|+
literal|"' in "
operator|+
name|centralizedVersionsFile
operator|.
name|getName
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
name|failures
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|comparison
operator|<
literal|0
condition|)
block|{
name|log
argument_list|(
literal|"OUT-OF-ORDER coordinate key '"
operator|+
name|currentKey
operator|+
literal|"' in "
operator|+
name|centralizedVersionsFile
operator|.
name|getName
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
name|failures
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|referencedCoordinateKeys
operator|.
name|put
argument_list|(
name|currentKey
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|previousKey
operator|=
name|currentKey
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Exception reading centralized versions file: "
operator|+
name|centralizedVersionsFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{ }
block|}
return|return
operator|!
name|failures
return|;
block|}
comment|/**    * Builds up logical {@link java.util.Properties} lines, composed of one non-blank,    * non-comment initial line, either:    *     * 1. without a non-escaped trailing slash; or    * 2. with a non-escaped trailing slash, followed by    *    zero or more lines with a non-escaped trailing slash, followed by    *    one or more lines without a non-escaped trailing slash    *    * All leading non-escaped whitespace and trailing non-escaped whitespace +    * non-escaped slash are trimmed from each line before concatenating.    *     * After composing the logical line, escaped characters are un-escaped.    *     * null is returned if there are no lines left to read.     */
DECL|method|readLogicalPropertiesLine
specifier|private
name|String
name|readLogicalPropertiesLine
parameter_list|(
name|BufferedReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|StringBuilder
name|logicalLine
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|line
decl_stmt|;
do|do
block|{
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|line
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
do|while
condition|(
name|BLANK_OR_COMMENT_LINE_PATTERN
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
do|;
name|Matcher
name|backslashMatcher
init|=
name|TRAILING_BACKSLASH_PATTERN
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
comment|// Check for a non-escaped backslash
if|if
condition|(
name|backslashMatcher
operator|.
name|find
argument_list|()
operator|&&
literal|1
operator|==
operator|(
name|backslashMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|length
argument_list|()
operator|%
literal|2
operator|)
condition|)
block|{
specifier|final
name|Matcher
name|firstLineMatcher
init|=
name|TRAILING_WHITESPACE_BACKSLASH_PATTERN
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstLineMatcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|logicalLine
operator|.
name|append
argument_list|(
name|firstLineMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// trim trailing backslash and any preceding whitespace
block|}
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
while|while
condition|(
literal|null
operator|!=
name|line
operator|&&
operator|(
name|backslashMatcher
operator|=
name|TRAILING_BACKSLASH_PATTERN
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|)
operator|.
name|find
argument_list|()
operator|&&
literal|1
operator|==
operator|(
name|backslashMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|length
argument_list|()
operator|%
literal|2
operator|)
condition|)
block|{
comment|// Trim leading whitespace, the trailing backslash and any preceding whitespace
specifier|final
name|Matcher
name|goodStuffMatcher
init|=
name|WHITESPACE_GOODSTUFF_WHITESPACE_BACKSLASH_PATTERN
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|goodStuffMatcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|logicalLine
operator|.
name|append
argument_list|(
name|goodStuffMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|line
condition|)
block|{
comment|// line can't have a non-escaped trailing backslash
specifier|final
name|Matcher
name|leadingWhitespaceMatcher
init|=
name|LEADING_WHITESPACE_PATTERN
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|leadingWhitespaceMatcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|line
operator|=
name|leadingWhitespaceMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// trim leading whitespace
block|}
name|logicalLine
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logicalLine
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
comment|// trim non-escaped leading whitespace
specifier|final
name|Matcher
name|leadingWhitespaceMatcher
init|=
name|LEADING_WHITESPACE_PATTERN
operator|.
name|matcher
argument_list|(
name|logicalLine
argument_list|)
decl_stmt|;
specifier|final
name|CharSequence
name|leadingWhitespaceStripped
init|=
name|leadingWhitespaceMatcher
operator|.
name|matches
argument_list|()
condition|?
name|leadingWhitespaceMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
else|:
name|logicalLine
decl_stmt|;
comment|// unescape all chars in the logical line
name|StringBuilder
name|output
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numChars
init|=
name|leadingWhitespaceStripped
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|numChars
operator|-
literal|1
condition|;
operator|++
name|pos
control|)
block|{
name|char
name|ch
init|=
name|leadingWhitespaceStripped
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'\\'
condition|)
block|{
name|ch
operator|=
name|leadingWhitespaceStripped
operator|.
name|charAt
argument_list|(
operator|++
name|pos
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numChars
operator|>
literal|0
condition|)
block|{
name|output
operator|.
name|append
argument_list|(
name|leadingWhitespaceStripped
operator|.
name|charAt
argument_list|(
name|numChars
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|output
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Check a single ivy.xml file for dependencies' versions in rev="${/org/name}"    * format.  Returns false if problems are found, true otherwise.    */
DECL|method|checkIvyXmlFile
specifier|private
name|boolean
name|checkIvyXmlFile
parameter_list|(
name|File
name|ivyXmlFile
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|log
argument_list|(
literal|"Scanning: "
operator|+
name|ivyXmlFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|verboseLevel
argument_list|)
expr_stmt|;
name|XMLReader
name|xmlReader
init|=
name|XMLReaderFactory
operator|.
name|createXMLReader
argument_list|()
decl_stmt|;
name|DependencyRevChecker
name|revChecker
init|=
operator|new
name|DependencyRevChecker
argument_list|(
name|ivyXmlFile
argument_list|)
decl_stmt|;
name|xmlReader
operator|.
name|setContentHandler
argument_list|(
name|revChecker
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|setErrorHandler
argument_list|(
name|revChecker
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
name|ivyXmlFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|!
name|revChecker
operator|.
name|fail
return|;
block|}
DECL|class|DependencyRevChecker
specifier|private
class|class
name|DependencyRevChecker
extends|extends
name|DefaultHandler
block|{
DECL|field|ivyXmlFile
specifier|private
specifier|final
name|File
name|ivyXmlFile
decl_stmt|;
DECL|field|tags
specifier|private
specifier|final
name|Stack
argument_list|<
name|String
argument_list|>
name|tags
init|=
operator|new
name|Stack
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|fail
specifier|public
name|boolean
name|fail
init|=
literal|false
decl_stmt|;
DECL|method|DependencyRevChecker
specifier|public
name|DependencyRevChecker
parameter_list|(
name|File
name|ivyXmlFile
parameter_list|)
block|{
name|this
operator|.
name|ivyXmlFile
operator|=
name|ivyXmlFile
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startElement
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"dependency"
argument_list|)
operator|&&
name|insideDependenciesTag
argument_list|()
condition|)
block|{
name|String
name|org
init|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"org"
argument_list|)
decl_stmt|;
name|boolean
name|foundAllAttributes
init|=
literal|true
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|org
condition|)
block|{
name|log
argument_list|(
literal|"MISSING 'org' attribute on<dependency> in "
operator|+
name|ivyXmlFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
name|fail
operator|=
literal|true
expr_stmt|;
name|foundAllAttributes
operator|=
literal|false
expr_stmt|;
block|}
name|String
name|name
init|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|name
condition|)
block|{
name|log
argument_list|(
literal|"MISSING 'name' attribute on<dependency> in "
operator|+
name|ivyXmlFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
name|fail
operator|=
literal|true
expr_stmt|;
name|foundAllAttributes
operator|=
literal|false
expr_stmt|;
block|}
name|String
name|rev
init|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"rev"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rev
condition|)
block|{
name|log
argument_list|(
literal|"MISSING 'rev' attribute on<dependency> in "
operator|+
name|ivyXmlFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
name|fail
operator|=
literal|true
expr_stmt|;
name|foundAllAttributes
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|foundAllAttributes
condition|)
block|{
name|String
name|coordinateKey
init|=
literal|"/"
operator|+
name|org
operator|+
literal|'/'
operator|+
name|name
decl_stmt|;
name|String
name|expectedRev
init|=
literal|"${"
operator|+
name|coordinateKey
operator|+
literal|'}'
decl_stmt|;
if|if
condition|(
operator|!
name|rev
operator|.
name|equals
argument_list|(
name|expectedRev
argument_list|)
condition|)
block|{
name|log
argument_list|(
literal|"BAD<dependency> 'rev' attribute value '"
operator|+
name|rev
operator|+
literal|"' - expected '"
operator|+
name|expectedRev
operator|+
literal|"'"
operator|+
literal|" in "
operator|+
name|ivyXmlFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
name|fail
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|referencedCoordinateKeys
operator|.
name|containsKey
argument_list|(
name|coordinateKey
argument_list|)
condition|)
block|{
name|log
argument_list|(
literal|"MISSING key '"
operator|+
name|coordinateKey
operator|+
literal|"' in "
operator|+
name|centralizedVersionsFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|Project
operator|.
name|MSG_ERR
argument_list|)
expr_stmt|;
name|fail
operator|=
literal|true
expr_stmt|;
block|}
name|referencedCoordinateKeys
operator|.
name|put
argument_list|(
name|coordinateKey
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
name|tags
operator|.
name|push
argument_list|(
name|localName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|endElement
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
name|tags
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
DECL|method|insideDependenciesTag
specifier|private
name|boolean
name|insideDependenciesTag
parameter_list|()
block|{
return|return
name|tags
operator|.
name|size
argument_list|()
operator|==
literal|2
operator|&&
name|tags
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|"ivy-module"
argument_list|)
operator|&&
name|tags
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
literal|"dependencies"
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit

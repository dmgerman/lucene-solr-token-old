begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
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
name|FilenameFilter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
comment|/**  *<p>  * An EntityProcessor instance which can stream file names found in a given base  * directory matching patterns and returning rows containing file information.  *</p>  *<p/>  *<p>  * It supports querying a give base directory by matching:  *<ul>  *<li>regular expressions to file names</li>  *<li>excluding certain files based on regular expression</li>  *<li>last modification date (newer or older than a given date or time)</li>  *<li>size (bigger or smaller than size given in bytes)</li>  *<li>recursively iterating through sub-directories</li>  *</ul>  * Its output can be used along with FileDataSource to read from files in file  * systems.  *</p>  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|FileListEntityProcessor
specifier|public
class|class
name|FileListEntityProcessor
extends|extends
name|EntityProcessorBase
block|{
DECL|field|fileName
DECL|field|baseDir
DECL|field|excludes
specifier|private
name|String
name|fileName
decl_stmt|,
name|baseDir
decl_stmt|,
name|excludes
decl_stmt|;
DECL|field|newerThan
DECL|field|olderThan
specifier|private
name|Date
name|newerThan
decl_stmt|,
name|olderThan
decl_stmt|;
DECL|field|biggerThan
DECL|field|smallerThan
specifier|private
name|long
name|biggerThan
init|=
operator|-
literal|1
decl_stmt|,
name|smallerThan
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|recursive
specifier|private
name|boolean
name|recursive
init|=
literal|false
decl_stmt|;
DECL|field|fileNamePattern
DECL|field|excludesPattern
specifier|private
name|Pattern
name|fileNamePattern
decl_stmt|,
name|excludesPattern
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|fileName
operator|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|FILE_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileName
operator|!=
literal|null
condition|)
block|{
name|fileName
operator|=
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|fileNamePattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
name|baseDir
operator|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|BASE_DIR
argument_list|)
expr_stmt|;
if|if
condition|(
name|baseDir
operator|==
literal|null
condition|)
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"'baseDir' is a required attribute"
argument_list|)
throw|;
name|baseDir
operator|=
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|baseDir
argument_list|)
expr_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"'baseDir' value: "
operator|+
name|baseDir
operator|+
literal|" is not a directory"
argument_list|)
throw|;
name|String
name|r
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|RECURSIVE
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
name|recursive
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|excludes
operator|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|EXCLUDES
argument_list|)
expr_stmt|;
if|if
condition|(
name|excludes
operator|!=
literal|null
condition|)
block|{
name|excludes
operator|=
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|excludes
argument_list|)
expr_stmt|;
name|excludesPattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|excludes
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDate
specifier|private
name|Date
name|getDate
parameter_list|(
name|String
name|dateStr
parameter_list|)
block|{
if|if
condition|(
name|dateStr
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Matcher
name|m
init|=
name|PLACE_HOLDER_PATTERN
operator|.
name|matcher
argument_list|(
name|dateStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
return|return
operator|(
name|Date
operator|)
name|resolver
operator|.
name|resolve
argument_list|(
name|dateStr
argument_list|)
return|;
block|}
name|m
operator|=
name|EvaluatorBag
operator|.
name|IN_SINGLE_QUOTES
operator|.
name|matcher
argument_list|(
name|dateStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|expr
init|=
literal|null
decl_stmt|;
name|expr
operator|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"NOW"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|EvaluatorBag
operator|.
name|dateMathParser
operator|.
name|parseMath
argument_list|(
name|expr
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|exp
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Invalid expression for date"
argument_list|,
name|exp
argument_list|)
throw|;
block|}
block|}
try|try
block|{
return|return
name|DataImporter
operator|.
name|DATE_TIME_FORMAT
operator|.
name|parse
argument_list|(
name|dateStr
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|exp
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Invalid expression for date"
argument_list|,
name|exp
argument_list|)
throw|;
block|}
block|}
DECL|method|nextRow
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextRow
parameter_list|()
block|{
if|if
condition|(
name|rowIterator
operator|!=
literal|null
condition|)
return|return
name|getAndApplyTrans
argument_list|()
return|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|fileDetails
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|)
decl_stmt|;
name|String
name|dateStr
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|NEWER_THAN
argument_list|)
decl_stmt|;
name|newerThan
operator|=
name|getDate
argument_list|(
name|dateStr
argument_list|)
expr_stmt|;
name|dateStr
operator|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|OLDER_THAN
argument_list|)
expr_stmt|;
name|olderThan
operator|=
name|getDate
argument_list|(
name|dateStr
argument_list|)
expr_stmt|;
name|getFolderFiles
argument_list|(
name|dir
argument_list|,
name|fileDetails
argument_list|)
expr_stmt|;
name|rowIterator
operator|=
name|fileDetails
operator|.
name|iterator
argument_list|()
expr_stmt|;
return|return
name|getAndApplyTrans
argument_list|()
return|;
block|}
DECL|method|getAndApplyTrans
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getAndApplyTrans
parameter_list|()
block|{
if|if
condition|(
name|rowcache
operator|!=
literal|null
condition|)
return|return
name|getFromRowCache
argument_list|()
return|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|getNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|r
operator|=
name|applyTransformer
argument_list|(
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
return|return
name|r
return|;
block|}
block|}
DECL|method|getFolderFiles
specifier|private
name|void
name|getFolderFiles
parameter_list|(
name|File
name|dir
parameter_list|,
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|fileDetails
parameter_list|)
block|{
comment|// Fetch an array of file objects that pass the filter, however the
comment|// returned array is never populated; accept() always returns false.
comment|// Rather we make use of the fileDetails array which is populated as
comment|// a side affect of the accept method.
name|dir
operator|.
name|list
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|File
name|fileObj
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileObj
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
name|recursive
condition|)
name|getFolderFiles
argument_list|(
name|fileObj
argument_list|,
name|fileDetails
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fileNamePattern
operator|==
literal|null
condition|)
block|{
name|addDetails
argument_list|(
name|fileDetails
argument_list|,
name|dir
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fileNamePattern
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|find
argument_list|()
condition|)
block|{
if|if
condition|(
name|excludesPattern
operator|!=
literal|null
operator|&&
name|excludesPattern
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|find
argument_list|()
condition|)
return|return
literal|false
return|;
name|addDetails
argument_list|(
name|fileDetails
argument_list|,
name|dir
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|addDetails
specifier|private
name|void
name|addDetails
parameter_list|(
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|files
parameter_list|,
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|details
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|File
name|aFile
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|aFile
operator|.
name|isDirectory
argument_list|()
condition|)
return|return;
name|long
name|sz
init|=
name|aFile
operator|.
name|length
argument_list|()
decl_stmt|;
name|Date
name|lastModified
init|=
operator|new
name|Date
argument_list|(
name|aFile
operator|.
name|lastModified
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|biggerThan
operator|!=
operator|-
literal|1
operator|&&
name|sz
operator|<=
name|biggerThan
condition|)
return|return;
if|if
condition|(
name|smallerThan
operator|!=
operator|-
literal|1
operator|&&
name|sz
operator|>=
name|smallerThan
condition|)
return|return;
if|if
condition|(
name|olderThan
operator|!=
literal|null
operator|&&
name|lastModified
operator|.
name|after
argument_list|(
name|olderThan
argument_list|)
condition|)
return|return;
if|if
condition|(
name|newerThan
operator|!=
literal|null
operator|&&
name|lastModified
operator|.
name|before
argument_list|(
name|newerThan
argument_list|)
condition|)
return|return;
name|details
operator|.
name|put
argument_list|(
name|DIR
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|details
operator|.
name|put
argument_list|(
name|FILE
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|details
operator|.
name|put
argument_list|(
name|ABSOLUTE_FILE
argument_list|,
name|aFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|details
operator|.
name|put
argument_list|(
name|SIZE
argument_list|,
name|sz
argument_list|)
expr_stmt|;
name|details
operator|.
name|put
argument_list|(
name|LAST_MODIFIED
argument_list|,
name|lastModified
argument_list|)
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|details
argument_list|)
expr_stmt|;
block|}
DECL|field|PLACE_HOLDER_PATTERN
specifier|public
specifier|static
specifier|final
name|Pattern
name|PLACE_HOLDER_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\$\\{.*?\\}"
argument_list|)
decl_stmt|;
DECL|field|DIR
specifier|public
specifier|static
specifier|final
name|String
name|DIR
init|=
literal|"fileDir"
decl_stmt|;
DECL|field|FILE
specifier|public
specifier|static
specifier|final
name|String
name|FILE
init|=
literal|"file"
decl_stmt|;
DECL|field|ABSOLUTE_FILE
specifier|public
specifier|static
specifier|final
name|String
name|ABSOLUTE_FILE
init|=
literal|"fileAbsolutePath"
decl_stmt|;
DECL|field|SIZE
specifier|public
specifier|static
specifier|final
name|String
name|SIZE
init|=
literal|"fileSize"
decl_stmt|;
DECL|field|LAST_MODIFIED
specifier|public
specifier|static
specifier|final
name|String
name|LAST_MODIFIED
init|=
literal|"fileLastModified"
decl_stmt|;
DECL|field|FILE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|FILE_NAME
init|=
literal|"fileName"
decl_stmt|;
DECL|field|BASE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|BASE_DIR
init|=
literal|"baseDir"
decl_stmt|;
DECL|field|EXCLUDES
specifier|public
specifier|static
specifier|final
name|String
name|EXCLUDES
init|=
literal|"excludes"
decl_stmt|;
DECL|field|NEWER_THAN
specifier|public
specifier|static
specifier|final
name|String
name|NEWER_THAN
init|=
literal|"newerThan"
decl_stmt|;
DECL|field|OLDER_THAN
specifier|public
specifier|static
specifier|final
name|String
name|OLDER_THAN
init|=
literal|"olderThan"
decl_stmt|;
DECL|field|BIGGER_THAN
specifier|public
specifier|static
specifier|final
name|String
name|BIGGER_THAN
init|=
literal|"biggerThan"
decl_stmt|;
DECL|field|SMALLER_THAN
specifier|public
specifier|static
specifier|final
name|String
name|SMALLER_THAN
init|=
literal|"smallerThan"
decl_stmt|;
DECL|field|RECURSIVE
specifier|public
specifier|static
specifier|final
name|String
name|RECURSIVE
init|=
literal|"recursive"
decl_stmt|;
block|}
end_class
end_unit

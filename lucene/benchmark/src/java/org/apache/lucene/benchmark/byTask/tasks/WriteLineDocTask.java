begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|DocMaker
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|StreamUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexableField
import|;
end_import
begin_comment
comment|/**  * A task which writes documents, one line per document. Each line is in the  * following format: title&lt;TAB&gt; date&lt;TAB&gt; body. The output of this  * task can be consumed by  * {@link org.apache.lucene.benchmark.byTask.feeds.LineDocSource} and is intended  * to save the IO overhead of opening a file per document to be indexed.  *<p>  * The format of the output is set according to the output file extension.  * Compression is recommended when the output file is expected to be large.  * See info on file extensions in  * {@link org.apache.lucene.benchmark.byTask.utils.StreamUtils.Type}  *<p>   * Supports the following parameters:  *<ul>  *<li><b>line.file.out</b> - the name of the file to write the output to. That  * parameter is mandatory.<b>NOTE:</b> the file is re-created.  *<li><b>line.fields</b> - which fields should be written in each line.  * (optional, default: {@link #DEFAULT_FIELDS}).  *<li><b>sufficient.fields</b> - list of field names, separated by comma, which,   * if all of them are missing, the document will be skipped. For example, to require   * that at least one of f1,f2 is not empty, specify: "f1,f2" in this field. To specify  * that no field is required, i.e. that even empty docs should be emitted, specify<b>","</b>.      * (optional, default: {@link #DEFAULT_SUFFICIENT_FIELDS}).  *</ul>  *<b>NOTE:</b> this class is not thread-safe and if used by multiple threads the  * output is unspecified (as all will write to the same output file in a  * non-synchronized way).  */
end_comment
begin_class
DECL|class|WriteLineDocTask
specifier|public
class|class
name|WriteLineDocTask
extends|extends
name|PerfTask
block|{
DECL|field|FIELDS_HEADER_INDICATOR
specifier|public
specifier|static
specifier|final
name|String
name|FIELDS_HEADER_INDICATOR
init|=
literal|"FIELDS_HEADER_INDICATOR###"
decl_stmt|;
DECL|field|SEP
specifier|public
specifier|final
specifier|static
name|char
name|SEP
init|=
literal|'\t'
decl_stmt|;
comment|/**    * Fields to be written by default    */
DECL|field|DEFAULT_FIELDS
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|DEFAULT_FIELDS
init|=
operator|new
name|String
index|[]
block|{
name|DocMaker
operator|.
name|TITLE_FIELD
block|,
name|DocMaker
operator|.
name|DATE_FIELD
block|,
name|DocMaker
operator|.
name|BODY_FIELD
block|,   }
decl_stmt|;
comment|/**    * Default fields which at least one of them is required to not skip the doc.    */
DECL|field|DEFAULT_SUFFICIENT_FIELDS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_SUFFICIENT_FIELDS
init|=
name|DocMaker
operator|.
name|TITLE_FIELD
operator|+
literal|','
operator|+
name|DocMaker
operator|.
name|BODY_FIELD
decl_stmt|;
DECL|field|docSize
specifier|private
name|int
name|docSize
init|=
literal|0
decl_stmt|;
DECL|field|lineFileOut
specifier|private
name|PrintWriter
name|lineFileOut
init|=
literal|null
decl_stmt|;
DECL|field|docMaker
specifier|private
name|DocMaker
name|docMaker
decl_stmt|;
DECL|field|threadBuffer
specifier|private
name|ThreadLocal
argument_list|<
name|StringBuilder
argument_list|>
name|threadBuffer
init|=
operator|new
name|ThreadLocal
argument_list|<
name|StringBuilder
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|threadNormalizer
specifier|private
name|ThreadLocal
argument_list|<
name|Matcher
argument_list|>
name|threadNormalizer
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Matcher
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|fieldsToWrite
specifier|private
specifier|final
name|String
index|[]
name|fieldsToWrite
decl_stmt|;
empty_stmt|;
DECL|field|sufficientFields
specifier|private
specifier|final
name|boolean
index|[]
name|sufficientFields
decl_stmt|;
DECL|field|checkSufficientFields
specifier|private
specifier|final
name|boolean
name|checkSufficientFields
decl_stmt|;
DECL|method|WriteLineDocTask
specifier|public
name|WriteLineDocTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
name|Config
name|config
init|=
name|runData
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|String
name|fname
init|=
name|config
operator|.
name|get
argument_list|(
literal|"line.file.out"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|fname
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"line.file.out must be set"
argument_list|)
throw|;
block|}
name|OutputStream
name|out
init|=
name|StreamUtils
operator|.
name|outputStream
argument_list|(
operator|new
name|File
argument_list|(
name|fname
argument_list|)
argument_list|)
decl_stmt|;
name|lineFileOut
operator|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
name|StreamUtils
operator|.
name|BUFFER_SIZE
argument_list|)
argument_list|)
expr_stmt|;
name|docMaker
operator|=
name|runData
operator|.
name|getDocMaker
argument_list|()
expr_stmt|;
comment|// init fields
name|String
name|f2r
init|=
name|config
operator|.
name|get
argument_list|(
literal|"line.fields"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|f2r
operator|==
literal|null
condition|)
block|{
name|fieldsToWrite
operator|=
name|DEFAULT_FIELDS
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|f2r
operator|.
name|indexOf
argument_list|(
name|SEP
argument_list|)
operator|>=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"line.fields "
operator|+
name|f2r
operator|+
literal|" should not contain the separator char: "
operator|+
name|SEP
argument_list|)
throw|;
block|}
name|fieldsToWrite
operator|=
name|f2r
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
comment|// init sufficient fields
name|sufficientFields
operator|=
operator|new
name|boolean
index|[
name|fieldsToWrite
operator|.
name|length
index|]
expr_stmt|;
name|String
name|suff
init|=
name|config
operator|.
name|get
argument_list|(
literal|"sufficient.fields"
argument_list|,
name|DEFAULT_SUFFICIENT_FIELDS
argument_list|)
decl_stmt|;
if|if
condition|(
literal|","
operator|.
name|equals
argument_list|(
name|suff
argument_list|)
condition|)
block|{
name|checkSufficientFields
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|checkSufficientFields
operator|=
literal|true
expr_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|sf
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|suff
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldsToWrite
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|sf
operator|.
name|contains
argument_list|(
name|fieldsToWrite
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|sufficientFields
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
name|writeHeader
argument_list|()
expr_stmt|;
block|}
comment|/**    * Write a header to the lines file - indicating how to read the file later     */
DECL|method|writeHeader
specifier|private
name|void
name|writeHeader
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
name|threadBuffer
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|sb
operator|==
literal|null
condition|)
block|{
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|threadBuffer
operator|.
name|set
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|FIELDS_HEADER_INDICATOR
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|f
range|:
name|fieldsToWrite
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|SEP
argument_list|)
operator|.
name|append
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
name|lineFileOut
operator|.
name|println
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLogMessage
specifier|protected
name|String
name|getLogMessage
parameter_list|(
name|int
name|recsCount
parameter_list|)
block|{
return|return
literal|"Wrote "
operator|+
name|recsCount
operator|+
literal|" line docs"
return|;
block|}
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
name|docSize
operator|>
literal|0
condition|?
name|docMaker
operator|.
name|makeDocument
argument_list|(
name|docSize
argument_list|)
else|:
name|docMaker
operator|.
name|makeDocument
argument_list|()
decl_stmt|;
name|Matcher
name|matcher
init|=
name|threadNormalizer
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|matcher
operator|==
literal|null
condition|)
block|{
name|matcher
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[\t\r\n]+"
argument_list|)
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|threadNormalizer
operator|.
name|set
argument_list|(
name|matcher
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|sb
init|=
name|threadBuffer
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|sb
operator|==
literal|null
condition|)
block|{
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|threadBuffer
operator|.
name|set
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|boolean
name|sufficient
init|=
operator|!
name|checkSufficientFields
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldsToWrite
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexableField
name|f
init|=
name|doc
operator|.
name|getField
argument_list|(
name|fieldsToWrite
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|f
operator|==
literal|null
condition|?
literal|""
else|:
name|matcher
operator|.
name|reset
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|" "
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|text
argument_list|)
operator|.
name|append
argument_list|(
name|SEP
argument_list|)
expr_stmt|;
name|sufficient
operator||=
name|text
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|sufficientFields
index|[
name|i
index|]
expr_stmt|;
block|}
if|if
condition|(
name|sufficient
condition|)
block|{
name|sb
operator|.
name|setLength
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// remove redundant last separator
comment|// lineFileOut is a PrintWriter, which synchronizes internally in println.
name|lineFileOut
operator|.
name|println
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|lineFileOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Set the params (docSize only)    * @param params docSize, or 0 for no limit.    */
annotation|@
name|Override
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|super
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|docSize
operator|=
operator|(
name|int
operator|)
name|Float
operator|.
name|parseFloat
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit

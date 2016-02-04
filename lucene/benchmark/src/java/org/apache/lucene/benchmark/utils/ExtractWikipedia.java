begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.benchmark.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|utils
package|;
end_package
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
name|Writer
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
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|ContentSource
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
name|feeds
operator|.
name|EnwikiContentSource
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
name|NoMoreDataException
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
name|util
operator|.
name|IOUtils
import|;
end_import
begin_comment
comment|/**  * Extract the downloaded Wikipedia dump into separate files for indexing.  */
end_comment
begin_class
DECL|class|ExtractWikipedia
specifier|public
class|class
name|ExtractWikipedia
block|{
DECL|field|outputDir
specifier|private
name|Path
name|outputDir
decl_stmt|;
DECL|field|count
specifier|static
specifier|public
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|field|BASE
specifier|static
specifier|final
name|int
name|BASE
init|=
literal|10
decl_stmt|;
DECL|field|docMaker
specifier|protected
name|DocMaker
name|docMaker
decl_stmt|;
DECL|method|ExtractWikipedia
specifier|public
name|ExtractWikipedia
parameter_list|(
name|DocMaker
name|docMaker
parameter_list|,
name|Path
name|outputDir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|outputDir
operator|=
name|outputDir
expr_stmt|;
name|this
operator|.
name|docMaker
operator|=
name|docMaker
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Deleting all files in "
operator|+
name|outputDir
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|rm
argument_list|(
name|outputDir
argument_list|)
expr_stmt|;
block|}
DECL|method|directory
specifier|public
name|Path
name|directory
parameter_list|(
name|int
name|count
parameter_list|,
name|Path
name|directory
parameter_list|)
block|{
if|if
condition|(
name|directory
operator|==
literal|null
condition|)
block|{
name|directory
operator|=
name|outputDir
expr_stmt|;
block|}
name|int
name|base
init|=
name|BASE
decl_stmt|;
while|while
condition|(
name|base
operator|<=
name|count
condition|)
block|{
name|base
operator|*=
name|BASE
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|<
name|BASE
condition|)
block|{
return|return
name|directory
return|;
block|}
name|directory
operator|=
name|directory
operator|.
name|resolve
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|base
operator|/
name|BASE
argument_list|)
argument_list|)
expr_stmt|;
name|directory
operator|=
name|directory
operator|.
name|resolve
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|count
operator|/
operator|(
name|base
operator|/
name|BASE
operator|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|directory
argument_list|(
name|count
operator|%
operator|(
name|base
operator|/
name|BASE
operator|)
argument_list|,
name|directory
argument_list|)
return|;
block|}
DECL|method|create
specifier|public
name|void
name|create
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|title
parameter_list|,
name|String
name|time
parameter_list|,
name|String
name|body
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|d
init|=
name|directory
argument_list|(
name|count
operator|++
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|Path
name|f
init|=
name|d
operator|.
name|resolve
argument_list|(
name|id
operator|+
literal|".txt"
argument_list|)
decl_stmt|;
name|StringBuilder
name|contents
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|contents
operator|.
name|append
argument_list|(
name|time
argument_list|)
expr_stmt|;
name|contents
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|contents
operator|.
name|append
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|contents
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|contents
operator|.
name|append
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|contents
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
try|try
init|(
name|Writer
name|writer
init|=
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|f
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
init|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|contents
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|extract
specifier|public
name|void
name|extract
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
literal|null
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting Extraction"
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
try|try
block|{
while|while
condition|(
operator|(
name|doc
operator|=
name|docMaker
operator|.
name|makeDocument
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|create
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|DocMaker
operator|.
name|ID_FIELD
argument_list|)
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|DocMaker
operator|.
name|TITLE_FIELD
argument_list|)
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|DocMaker
operator|.
name|DATE_FIELD
argument_list|)
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoMoreDataException
name|e
parameter_list|)
block|{
comment|//continue
block|}
name|long
name|finish
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Extraction took "
operator|+
operator|(
name|finish
operator|-
name|start
operator|)
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|wikipedia
init|=
literal|null
decl_stmt|;
name|Path
name|outputDir
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|"enwiki"
argument_list|)
decl_stmt|;
name|boolean
name|keepImageOnlyDocs
init|=
literal|true
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|arg
init|=
name|args
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"--input"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"-i"
argument_list|)
condition|)
block|{
name|wikipedia
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|args
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"--output"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"-o"
argument_list|)
condition|)
block|{
name|outputDir
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|args
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"--discardImageOnlyDocs"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"-d"
argument_list|)
condition|)
block|{
name|keepImageOnlyDocs
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"docs.file"
argument_list|,
name|wikipedia
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"content.source.forever"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"keep.image.only.docs"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|keepImageOnlyDocs
argument_list|)
argument_list|)
expr_stmt|;
name|Config
name|config
init|=
operator|new
name|Config
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|ContentSource
name|source
init|=
operator|new
name|EnwikiContentSource
argument_list|()
decl_stmt|;
name|source
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|DocMaker
name|docMaker
init|=
operator|new
name|DocMaker
argument_list|()
decl_stmt|;
name|docMaker
operator|.
name|setConfig
argument_list|(
name|config
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|docMaker
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|wikipedia
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Extracting Wikipedia to: "
operator|+
name|outputDir
operator|+
literal|" using EnwikiContentSource"
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|outputDir
argument_list|)
expr_stmt|;
name|ExtractWikipedia
name|extractor
init|=
operator|new
name|ExtractWikipedia
argument_list|(
name|docMaker
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|extractor
operator|.
name|extract
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|printUsage
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|printUsage
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: java -cp<...> org.apache.lucene.benchmark.utils.ExtractWikipedia --input|-i<Path to Wikipedia XML file> "
operator|+
literal|"[--output|-o<Output Path>] [--discardImageOnlyDocs|-d]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"--discardImageOnlyDocs tells the extractor to skip Wiki docs that contain only images"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit

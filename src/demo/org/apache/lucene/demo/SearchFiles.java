begin_unit
begin_package
DECL|package|org.apache.lucene.demo
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|util
operator|.
name|Date
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|IndexReader
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
name|FilterIndexReader
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
name|search
operator|.
name|Searcher
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|Hits
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
name|queryParser
operator|.
name|QueryParser
import|;
end_import
begin_comment
comment|/** Simple command-line based search demo. */
end_comment
begin_class
DECL|class|SearchFiles
specifier|public
class|class
name|SearchFiles
block|{
comment|/** Use the norms from one field for all fields.  Norms are read into memory,    * using a byte of memory per document per searched field.  This can cause    * search of large collections with a large number of fields to run out of    * memory.  If all of the fields contain only a single token, then the norms    * are all identical, then single norm vector may be shared. */
DECL|class|OneNormsReader
specifier|private
specifier|static
class|class
name|OneNormsReader
extends|extends
name|FilterIndexReader
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|method|OneNormsReader
specifier|public
name|OneNormsReader
parameter_list|(
name|IndexReader
name|in
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|norms
specifier|public
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|norms
argument_list|(
name|this
operator|.
name|field
argument_list|)
return|;
block|}
block|}
DECL|method|SearchFiles
specifier|private
name|SearchFiles
parameter_list|()
block|{}
comment|/** Simple command-line based search demo. */
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
name|String
name|usage
init|=
literal|"Usage: java org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-raw] [-norms field]"
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
operator|&&
operator|(
literal|"-h"
operator|.
name|equals
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
operator|||
literal|"-help"
operator|.
name|equals
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
operator|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|String
name|index
init|=
literal|"index"
decl_stmt|;
name|String
name|field
init|=
literal|"contents"
decl_stmt|;
name|String
name|queries
init|=
literal|null
decl_stmt|;
name|int
name|repeat
init|=
literal|0
decl_stmt|;
name|boolean
name|raw
init|=
literal|false
decl_stmt|;
name|String
name|normsField
init|=
literal|null
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
if|if
condition|(
literal|"-index"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|index
operator|=
name|args
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-field"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|field
operator|=
name|args
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-queries"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|queries
operator|=
name|args
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-repeat"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|repeat
operator|=
name|Integer
operator|.
name|parseInt
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
literal|"-raw"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|raw
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-norms"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|normsField
operator|=
name|args
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|normsField
operator|!=
literal|null
condition|)
name|reader
operator|=
operator|new
name|OneNormsReader
argument_list|(
name|reader
argument_list|,
name|normsField
argument_list|)
expr_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|()
decl_stmt|;
name|BufferedReader
name|in
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|queries
operator|!=
literal|null
condition|)
block|{
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|queries
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|queries
operator|==
literal|null
condition|)
comment|// prompt the user
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"Query: "
argument_list|)
expr_stmt|;
name|String
name|line
init|=
name|in
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
operator|||
name|line
operator|.
name|length
argument_list|()
operator|==
operator|-
literal|1
condition|)
break|break;
name|Query
name|query
init|=
name|QueryParser
operator|.
name|parse
argument_list|(
name|line
argument_list|,
name|field
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Searching for: "
operator|+
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|repeat
operator|>
literal|0
condition|)
block|{
comment|// repeat& time as benchmark
name|Date
name|start
init|=
operator|new
name|Date
argument_list|()
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
name|repeat
condition|;
name|i
operator|++
control|)
block|{
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
name|Date
name|end
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Time: "
operator|+
operator|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|hits
operator|.
name|length
argument_list|()
operator|+
literal|" total matching documents"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|HITS_PER_PAGE
init|=
literal|10
decl_stmt|;
for|for
control|(
name|int
name|start
init|=
literal|0
init|;
name|start
operator|<
name|hits
operator|.
name|length
argument_list|()
condition|;
name|start
operator|+=
name|HITS_PER_PAGE
control|)
block|{
name|int
name|end
init|=
name|Math
operator|.
name|min
argument_list|(
name|hits
operator|.
name|length
argument_list|()
argument_list|,
name|start
operator|+
name|HITS_PER_PAGE
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|raw
condition|)
block|{
comment|// output raw format
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"doc="
operator|+
name|hits
operator|.
name|id
argument_list|(
name|i
argument_list|)
operator|+
literal|" score="
operator|+
name|hits
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|Document
name|doc
init|=
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|doc
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|". "
operator|+
name|path
argument_list|)
expr_stmt|;
name|String
name|title
init|=
name|doc
operator|.
name|get
argument_list|(
literal|"title"
argument_list|)
decl_stmt|;
if|if
condition|(
name|title
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"   Title: "
operator|+
name|doc
operator|.
name|get
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|". "
operator|+
literal|"No path for this document"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|queries
operator|!=
literal|null
condition|)
comment|// non-interactive
break|break;
if|if
condition|(
name|hits
operator|.
name|length
argument_list|()
operator|>
name|end
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"more (y/n) ? "
argument_list|)
expr_stmt|;
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|line
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'n'
condition|)
break|break;
block|}
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit

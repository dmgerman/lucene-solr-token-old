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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|File
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
name|io
operator|.
name|InputStreamReader
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
name|queryParser
operator|.
name|QueryParser
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
name|Collector
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
name|ScoreDoc
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
name|Scorer
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
name|TopScoreDocCollector
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
name|store
operator|.
name|FSDirectory
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
name|Version
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
literal|"Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-raw] [-norms field] [-paging hitsPerPage]"
decl_stmt|;
name|usage
operator|+=
literal|"\n\tSpecify 'false' for hitsPerPage to use streaming instead of paging search."
expr_stmt|;
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
name|boolean
name|paging
init|=
literal|true
decl_stmt|;
name|int
name|hitsPerPage
init|=
literal|10
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
elseif|else
if|if
condition|(
literal|"-paging"
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
if|if
condition|(
name|args
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
condition|)
block|{
name|paging
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|hitsPerPage
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
if|if
condition|(
name|hitsPerPage
operator|==
literal|0
condition|)
block|{
name|paging
operator|=
literal|false
expr_stmt|;
block|}
block|}
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
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|index
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// only searching, so read-only=true
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
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
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
name|QueryParser
name|parser
init|=
operator|new
name|QueryParser
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|field
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
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
name|println
argument_list|(
literal|"Enter query: "
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
name|line
operator|=
name|line
operator|.
name|trim
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
condition|)
break|break;
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
name|line
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
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|100
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
if|if
condition|(
name|paging
condition|)
block|{
name|doPagingSearch
argument_list|(
name|in
argument_list|,
name|searcher
argument_list|,
name|query
argument_list|,
name|hitsPerPage
argument_list|,
name|raw
argument_list|,
name|queries
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doStreamingSearch
argument_list|(
name|searcher
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * This method uses a custom HitCollector implementation which simply prints out    * the docId and score of every matching document.     *     *  This simulates the streaming search use case, where all hits are supposed to    *  be processed, regardless of their relevance.    */
DECL|method|doStreamingSearch
specifier|public
specifier|static
name|void
name|doStreamingSearch
parameter_list|(
specifier|final
name|Searcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|Collector
name|streamingHitCollector
init|=
operator|new
name|Collector
argument_list|()
block|{
specifier|private
name|Scorer
name|scorer
decl_stmt|;
specifier|private
name|int
name|docBase
decl_stmt|;
comment|// simply print docId and score of every matching document
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"doc="
operator|+
name|doc
operator|+
name|docBase
operator|+
literal|" score="
operator|+
name|scorer
operator|.
name|score
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|docBase
operator|=
name|docBase
expr_stmt|;
block|}
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
block|}
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|streamingHitCollector
argument_list|)
expr_stmt|;
block|}
comment|/**    * This demonstrates a typical paging search scenario, where the search engine presents     * pages of size n to the user. The user can then go to the next page if interested in    * the next hits.    *     * When the query is executed for the first time, then only enough results are collected    * to fill 5 result pages. If the user wants to page beyond this limit, then the query    * is executed another time and all hits are collected.    *     */
DECL|method|doPagingSearch
specifier|public
specifier|static
name|void
name|doPagingSearch
parameter_list|(
name|BufferedReader
name|in
parameter_list|,
name|Searcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|,
name|int
name|hitsPerPage
parameter_list|,
name|boolean
name|raw
parameter_list|,
name|boolean
name|interactive
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Collect enough docs to show 5 pages
name|TopScoreDocCollector
name|collector
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
literal|5
operator|*
name|hitsPerPage
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|collector
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
decl_stmt|;
name|int
name|numTotalHits
init|=
name|collector
operator|.
name|getTotalHits
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|numTotalHits
operator|+
literal|" total matching documents"
argument_list|)
expr_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
name|int
name|end
init|=
name|Math
operator|.
name|min
argument_list|(
name|numTotalHits
argument_list|,
name|hitsPerPage
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|end
operator|>
name|hits
operator|.
name|length
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Only results 1 - "
operator|+
name|hits
operator|.
name|length
operator|+
literal|" of "
operator|+
name|numTotalHits
operator|+
literal|" total matching documents collected."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Collect more (y/n) ?"
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
block|{
break|break;
block|}
name|collector
operator|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|numTotalHits
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|hits
operator|=
name|collector
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
expr_stmt|;
block|}
name|end
operator|=
name|Math
operator|.
name|min
argument_list|(
name|hits
operator|.
name|length
argument_list|,
name|start
operator|+
name|hitsPerPage
argument_list|)
expr_stmt|;
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
index|[
name|i
index|]
operator|.
name|doc
operator|+
literal|" score="
operator|+
name|hits
index|[
name|i
index|]
operator|.
name|score
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|hits
index|[
name|i
index|]
operator|.
name|doc
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
operator|!
name|interactive
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|numTotalHits
operator|>=
name|end
condition|)
block|{
name|boolean
name|quit
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"Press "
argument_list|)
expr_stmt|;
if|if
condition|(
name|start
operator|-
name|hitsPerPage
operator|>=
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"(p)revious page, "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|start
operator|+
name|hitsPerPage
operator|<
name|numTotalHits
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"(n)ext page, "
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"(q)uit or enter number to jump to a page."
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
literal|'q'
condition|)
block|{
name|quit
operator|=
literal|true
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|line
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'p'
condition|)
block|{
name|start
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|start
operator|-
name|hitsPerPage
argument_list|)
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'n'
condition|)
block|{
if|if
condition|(
name|start
operator|+
name|hitsPerPage
operator|<
name|numTotalHits
condition|)
block|{
name|start
operator|+=
name|hitsPerPage
expr_stmt|;
block|}
break|break;
block|}
else|else
block|{
name|int
name|page
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|page
operator|-
literal|1
operator|)
operator|*
name|hitsPerPage
operator|<
name|numTotalHits
condition|)
block|{
name|start
operator|=
operator|(
name|page
operator|-
literal|1
operator|)
operator|*
name|hitsPerPage
expr_stmt|;
break|break;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No such page"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|quit
condition|)
break|break;
name|end
operator|=
name|Math
operator|.
name|min
argument_list|(
name|numTotalHits
argument_list|,
name|start
operator|+
name|hitsPerPage
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
